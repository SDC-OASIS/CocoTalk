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
    var socket: BehaviorRelay<WebSocketHelper?> { get }
    var userId2RoomMember: BehaviorRelay<[Int:RoomMember]> { get }
    var rawMessages: BehaviorRelay<[ModelMessage]> { get }
    var bundleInfo: BehaviorRelay<ModelMessageBundle?> { get }
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
        var socket = BehaviorRelay<WebSocketHelper?>(value: nil)
        var userId2RoomMember = BehaviorRelay<[Int:RoomMember]>(value: [:])
        var rawMessages = BehaviorRelay<[ModelMessage]>(value: [])
        var bundleInfo = BehaviorRelay<ModelMessageBundle?>(value: nil)
    }
    
    struct Output: ChatRoomOutput {
        var messages = BehaviorRelay<[ModelMessage]>(value: [])
        var members = BehaviorRelay<[RoomMember]>(value: [])
        var roomInfo = BehaviorRelay<ModelRoom?>(value: nil)
    }
    
    // MARK: - Calculate read count
    func calculateReadMemberCount(_ messageSentAt: String) -> Int {
        let sentAt = (messageSentAt.parseDate() ?? Date(timeIntervalSince1970: 0)).timeIntervalSince1970
        let members = output.roomInfo.value?.members ?? []
        
        var count = members.count
        
        for member in members {
            let enteredAt = (member.enteredAt?.parseDate() ?? Date(timeIntervalSince1970: 0)).timeIntervalSince1970
            let awayAt = (member.awayAt?.parseDate() ?? Date(timeIntervalSince1970: 0)).timeIntervalSince1970
            
            print("-----------------------------------")
            print("username: \(member.username ?? "")")
            if enteredAt > awayAt { // 현재 접속중
                print("🟢 채팅방 안 입니다.")
                count -= 1
            } else if awayAt > sentAt { // 읽은 사람이 나간 경우
                print("🔴 채팅방 밖 입니다.")
                count -= 1
            }
            print("enteredAt: \(member.enteredAt?.parseDate())")
            print("awayAt: \(member.awayAt?.parseDate())")
            print("sentAt: \(messageSentAt.parseDate())")
        }
        print("totalCount: \(members.count)\tunreadCount: \(count)")
        print("==================================")
        return count
    }
    
    // MARK: - Process Messages
    func getProcessedMessages() -> [ModelMessage] {
        var processedMessages = dependency.rawMessages.value
        
        for (index, message) in processedMessages.enumerated() {
            print("processing... \(message.content ?? "")")
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
    
    // MARK: - Build message
    private func buildMessage(_ room: ModelRoom) -> ModelPubChatMessage? {
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
                                       type: 0,
                                       content: input.text.value)
        }
        return nil
    }
    
    // MARK: - Convert Message
    func convertMessage(_ message: ModelMessageVO) -> ModelMessage? {
        guard let subMessage = message.message else {
            return nil
        }
        
        dependency.bundleInfo.accept(message.bundleInfo)
        
        return ModelMessage(id: subMessage.id,
                            roomId: subMessage.roomId,
                            messageBundleId: subMessage.messageBundleId,
                            userId: subMessage.userId,
                            content: subMessage.content,
                            type: subMessage.type,
                            sentAt: subMessage.sentAt,
                            mediaUrls: [],
                            senderId: nil,
                            date: nil,
                            isMe: nil,
                            hasTail: nil,
                            hasDate: nil,
                            username: nil,
                            profileImageURL: nil)
    }
}

extension ChatRoomViewModel {
    // MARK: - Fetch Message
    func getMessages() {
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
                      let data = response.data else {
                    return
                }
                self.dependency.rawMessages.accept(data.messageList ?? [])
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
    
}
