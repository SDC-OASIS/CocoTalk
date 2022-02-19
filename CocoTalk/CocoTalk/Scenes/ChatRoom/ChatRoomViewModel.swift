//
//  ChatRoomViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/19.
//


import Foundation
import RxSwift
import RxRelay
import SwiftKeychainWrapper

protocol ChatRoomInput {
    var text: BehaviorRelay<String> { get }
    var type: BehaviorRelay<MessageTypeEnum> { get }
}

protocol ChatRoomDependency {
    var isLoading: BehaviorRelay<Bool> { get }
    var hasFirstMessage: BehaviorRelay<Bool> { get }
    var loadSocketFailed: BehaviorRelay<Bool> { get }
    
    var socket: BehaviorRelay<WebSocketHelper?> { get }
    var userId2RoomMember: BehaviorRelay<[Int:RoomMember]> { get }
    var rawMessages: BehaviorRelay<[ModelMessage]> { get }
    var prevMessages: BehaviorRelay<[ModelMessage]> { get }
    var bundleInfo: BehaviorRelay<ModelMessageBundle?> { get }
    var currentOldestMessageId: BehaviorRelay<String?> { get }
    
    var isUploadingMediaFile: BehaviorRelay<Bool> { get }
    var uploadingMediaFileUUIDList: BehaviorRelay<[UUID]> { get }
    var uploadingMediaFileUUID: BehaviorRelay<UUID?> { get }
    var failedToSendMedia: BehaviorRelay<UUID?> { get }
    var successToSendMedia: BehaviorRelay<UUID?> { get }
    var postedMediaFileURL: BehaviorRelay<String?> { get }
}

protocol ChatRoomOutput {
    var messages: BehaviorRelay<[ModelMessage]> { get }
    var members: BehaviorRelay<[RoomMember]> { get }
    var roomInfo: BehaviorRelay<ModelRoom?> { get }
}

class ChatRoomViewModel {
    
    var chatRoomRepository = ChatRoomRepository()
    var messageRepository = MessageRepository()
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    var rooomId: String
    
    init(roomId: String, members: [RoomMember]) {
        self.rooomId = roomId
        self.output.members.accept(members)
    }
    
    struct Input: ChatRoomInput {
        var type = BehaviorRelay<MessageTypeEnum>(value: .text)
        var text = BehaviorRelay<String>(value: "")
    }
    
    struct Dependency: ChatRoomDependency {
        var isLoading = BehaviorRelay<Bool>(value: false)
        var hasFirstMessage = BehaviorRelay<Bool>(value: false)
        var loadSocketFailed = BehaviorRelay<Bool>(value: false)
        
        var socket = BehaviorRelay<WebSocketHelper?>(value: nil)
        var userId2RoomMember = BehaviorRelay<[Int:RoomMember]>(value: [:])
        var rawMessages = BehaviorRelay<[ModelMessage]>(value: [])
        var prevMessages = BehaviorRelay<[ModelMessage]>(value: [])
        var bundleInfo = BehaviorRelay<ModelMessageBundle?>(value: nil)
        var currentOldestMessageId = BehaviorRelay<String?>(value: nil)
        
        var failedToSendMedia = BehaviorRelay<UUID?>(value: nil)
        var successToSendMedia = BehaviorRelay<UUID?>(value: nil)
        var isUploadingMediaFile = BehaviorRelay<Bool>(value: false)
        var uploadingMediaFileUUIDList = BehaviorRelay<[UUID]>(value: [])
        var uploadingMediaFileUUID = BehaviorRelay<UUID?>(value: nil)
        var postedMediaFileURL = BehaviorRelay<String?>(value: nil)
    }
    
    struct Output: ChatRoomOutput {
        var messages = BehaviorRelay<[ModelMessage]>(value: [])
        var members = BehaviorRelay<[RoomMember]>(value: [])
        var roomInfo = BehaviorRelay<ModelRoom?>(value: nil)
    }
    
    // MARK: - Count messages in the same bundle.
    func countMessageInBundle(bundleId: String) -> Int {
        var count = 0
        for message in dependency.rawMessages.value {
            if message.messageBundleId == bundleId {
                count += 1
            }
        }
        return  20-count
    }
    
    // MARK: - Calculate read count
    func calculateReadMemberCount(_ messageSentAt: String) -> Int {
        let sentAt = (messageSentAt.parseDate() ?? Date(timeIntervalSince1970: 0)).timeIntervalSince1970
        let members = output.roomInfo.value?.members ?? []
        
        var count = members.count
        
        for member in members {
            let enteredAt = (member.enteredAt?.parseDate() ?? Date(timeIntervalSince1970: 0)).timeIntervalSince1970
            let awayAt = (member.awayAt?.parseDate() ?? Date(timeIntervalSince1970: 0)).timeIntervalSince1970

            if enteredAt > awayAt { // 현재 접속중
                count -= 1
            } else if awayAt > sentAt { // 읽은 사람이 나간 경우
                count -= 1
            }
        }
        return count
    }
    
    
    // MARK: - Process Messages
    func getProcessedMessages() -> [ModelMessage] {
        var processedMessages = dependency.rawMessages.value
        
        for (index, message) in processedMessages.enumerated() {
            var newMessage = message
            
            if let savedData = UserDefaults.standard.object(forKey: UserDefaultsKey.myData.rawValue) as? Data,
               let data = try? JSONDecoder().decode(ModelSignupResponse.self, from: savedData),
               let userId = newMessage.userId,
               let roomMember = dependency.userId2RoomMember.value[userId] {
                
                let profileString = roomMember.profile ?? ""
                let profileData =  try? JSONDecoder().decode(PlainStringProfile.self, from: Data(profileString.utf8))
                newMessage.profileImageURL = profileData?.profile
                newMessage.username = roomMember.username
                
                // 내가 보낸 채팅인지 확인
                if newMessage.userId == data.id {
                    newMessage.isMe = true
                }
                
                if index < processedMessages.count-1 {
                    let nextMessage = processedMessages[index+1]
                    if nextMessage.userId != newMessage.userId {
                        newMessage.hasDate = true
                    } else {
                        let nextDate = nextMessage.sentAt?.parseDate()
                        let nextDateTimestamp = nextDate?.timeIntervalSince1970 ?? -1
                        let nextMin = nextDate?.getMinute()
                        let msgDate = newMessage.sentAt?.parseDate()
                        let msgDateTimestamp = msgDate?.timeIntervalSince1970 ?? -1
                        let msgMin = msgDate?.getMinute()
                        
                        if nextMin != msgMin || nextDateTimestamp - msgDateTimestamp > 3540  {
                            newMessage.hasDate = true
                            processedMessages[index+1].hasTail = true
                        }
                    }
                } else if index == processedMessages.count - 1 {
                    newMessage.hasDate = true
                }
                
                // 이전 메시지와 사용자가 다른지 -> 프로필 표시 여부
                if index > 0 {
                    let prevMessage = processedMessages[index - 1]
                    if prevMessage.userId != newMessage.userId {
                        newMessage.hasTail = true
                    }
                } else if index == 0 {
                    newMessage.hasTail = true
                }
            }
            // 읽은 사람 수 표시
            newMessage.unreadMemberCount = calculateReadMemberCount(newMessage.sentAt ?? "")
            
            processedMessages[index] = newMessage
        }
        return processedMessages
    }
    
    // MARK: - Build ModelPubChatMessage with ModelRoom
    func buildMessage(_ room: ModelRoom, content: String? = nil, type: Int? = nil) -> ModelPubChatMessage? {
        if let savedData = UserDefaults.standard.object(forKey: UserDefaultsKey.myData.rawValue) as? Data,
           let data = try? JSONDecoder().decode(ModelSignupResponse.self, from: savedData) {
            
            guard let roomId = room.id else {
                return nil
            }
            
            let rooms = ChatRoomRepository.items.filter { ($0.room?.id ?? "") == roomId }
            guard rooms.count > 0 else {
                return nil
            }

            var nextBundleId: String
            if let bundleId = dependency.bundleInfo.value?.nextMessageBundleId {
                nextBundleId = bundleId
            } else {
                let messageBundleIds: String = rooms[0].room?.messageBundleIds ?? ""
                nextBundleId = messageBundleIds.parseMessageBundleIds()?.last ?? ""
            }

            return ModelPubChatMessage(roomId: room.id ?? "",
                                       roomType: room.type ?? 0,
                                       roomname: room.roomname ?? "",
                                       userId: data.id ?? -1,
                                       username: data.username ?? "",
                                       messageBundleId: nextBundleId,
                                       receiverIds: room.members?.map { "\($0.userId ?? -1)" },
                                       type: type ?? 0,
                                       content: content ?? input.text.value)
        }
        return nil
    }
    
    // MARK: - Build ModelMessage with ModelSubChatMessage
    func buildMessage(_ subMessage: ModelSubChatMessage) -> ModelMessage? {
        let username = dependency.userId2RoomMember.value[subMessage.userId ?? 0]?.username ?? ""
        return ModelMessage(id: subMessage.id,
                            roomId: rooomId,
                            messageBundleId: subMessage.messageBundleId,
                            userId: subMessage.userId,
                            content: subMessage.content,
                            type: subMessage.type,
                            sentAt: subMessage.sentAt,
                            username: username)
    }
    
    // MARK: - Convert Message
    func convertMessage(_ message: ModelMessageVO) -> ModelMessage? {
        guard let subMessage = message.message else {
            return nil
        }
        dependency.bundleInfo.accept(message.bundleInfo)
        return buildMessage(subMessage)
    }
}

extension ChatRoomViewModel {
    // MARK: - Initial Message Fetch
    func initailMessageFetch() {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        
        var count = 0
        ChatRoomRepository.chatRooms.forEach {
            if ($0.room?.id ?? "") == rooomId {
                count = $0.recentMessageBundleCount ?? 0
            }
        }
        
        chatRoomRepository.fetchInitialMessages(with: token, roomId: rooomId, count: count)
            .subscribe(onNext: { [weak self] response in
                guard let self = self,
                      let data = response.data,
                      let messages = data.messageList,
                      messages.count > 0 else {
                    return
                }
                self.dependency.currentOldestMessageId.accept(messages[0].id)
                self.dependency.rawMessages.accept(messages)
            })
            .disposed(by: bag)
    }
    
    // MARK: - Fetch Room
    func fetchRoomInfo() {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        
        dependency.isLoading.accept(true)
        chatRoomRepository.fetchRoomInfo(with: token, roomId: self.rooomId)
            .subscribe(onNext: { [weak self] response in
                self?.dependency.isLoading.accept(false)
                guard let self = self,
                      let room = response.data else {
                    return
                }
                self.output.roomInfo.accept(room)
            }).disposed(by: bag)
    }
    
    // MARK: - Send message
    func sendMessage() {
        if input.text.value.isEmpty {
            return
        }
        
        guard let socket = dependency.socket.value,
              let roomInfo = output.roomInfo.value else {
                  return
              }

        if let message = buildMessage(roomInfo) {
              socket.sendMessage(message)
        }
    }
    
    func sendMedia(_ message: ModelPubChatMessage) {
        guard let socket = dependency.socket.value else {
            return
        }
        socket.sendMessage(message)
    }
    
    // MARK: - Fetch preveious messages
    func fetchPreviousMessages() {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        
        let rawMessages = dependency.rawMessages.value
        guard rawMessages.count > 0 else {
            return
        }
        dependency.isLoading.accept(true)
        
        // 최상단 메시지의 번들 id
        let oldestBundleId = rawMessages[0].messageBundleId ?? ""
        // 카운트
        let bundleCount = countMessageInBundle(bundleId: oldestBundleId)
        // roomId
        let roomId = output.roomInfo.value?.id ?? ""
        chatRoomRepository.fetchPrevMessages(with: token, roomId: roomId, bundleId: oldestBundleId, count: bundleCount)
            .subscribe(onNext: { [weak self] response in
                self?.dependency.isLoading.accept(false)
                guard let self = self,
                      let messages = response.data,
                      messages.count > 0,
                      self.dependency.rawMessages.value.count > 0  else {
                          self?.dependency.prevMessages.accept([])
                          return
                      }
                
                if let id = messages[0].id,
                   let prevOldestId = self.dependency.currentOldestMessageId.value,
                   id == prevOldestId {
                    self.dependency.hasFirstMessage.accept(true)
                    return
                }
                
                let modelMessages = messages.compactMap { self.buildMessage($0) }
                
                self.dependency.currentOldestMessageId.accept(messages[0].id)
                self.dependency.prevMessages.accept(modelMessages)
            }).disposed(by: bag)
    }
    
    func postMedia(mediaFile: Data?, mediaThumbnail: Data? = nil) {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        
        dependency.isUploadingMediaFile.accept(true)
        let oldVal = dependency.uploadingMediaFileUUIDList.value
        var newVal = oldVal
        let newFileUUID = UUID()
        newVal.append(newFileUUID)
        dependency.uploadingMediaFileUUIDList.accept(newVal)
        
        chatRoomRepository.sendMediaFile(with: token, roomId: rooomId, mediaFile: mediaFile ?? Data(), mediaThumbnail: mediaThumbnail ?? Data())
            .subscribe(onNext: { [weak self, newFileUUID] response in
                self?.dependency.isUploadingMediaFile.accept(false)
                guard let self = self,
                      let mediaUrl = response.data else {
                          self?.dependency.failedToSendMedia.accept(newFileUUID)
                    return
                }
                self.dependency.postedMediaFileURL.accept(mediaUrl)
                self.dependency.successToSendMedia.accept(newFileUUID)
            }).disposed(by: bag)
    }
}
