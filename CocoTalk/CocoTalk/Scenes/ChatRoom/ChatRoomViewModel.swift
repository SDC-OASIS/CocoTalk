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
    
    private var rawMessages: [ModelMessage] = []
    
    init(roomId: String, members: [RoomMember]) {
        self.rooomId = roomId
        self.output.members.accept(members)
        
//        var newValue = dependency.userId2RoomMember.value
//        members.forEach {
//            newValue[$0.userId ?? -1] = $0
//        }
//        dependency.userId2RoomMember.accept(newValue)
    }
    
    struct Input: ChatRoomInput {
        var type = BehaviorRelay<MessageTypeEnum>(value: .text)
        var text = BehaviorRelay<String>(value: "")
    }
    
    struct Dependency: ChatRoomDependency {
        var isLoading = BehaviorRelay<Bool>(value: false)
        var socket = BehaviorRelay<WebSocketHelper?>(value: nil)
        var userId2RoomMember = BehaviorRelay<[Int:RoomMember]>(value: [:])
    }
    
    struct Output: ChatRoomOutput {
        var messages = BehaviorRelay<[ModelMessage]>(value: [])
        var members = BehaviorRelay<[RoomMember]>(value: [])
        var roomInfo = BehaviorRelay<ModelRoom?>(value: nil)
    }
    
    // MARK: - Helpers
    private func getProcessedMessages() -> [ModelMessage] {
        return self.rawMessages.enumerated().map { setMessage($1, index: $0) }
    }
    
    #warning("내 데이터 뷰모델에서 갖고 있게 짜기")
    private func setMessage(_ message: ModelMessage, index: Int) -> ModelMessage {
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
            
            if index < self.rawMessages.count-1 {
                let nextMessage = self.rawMessages[index+1]
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
                        self.rawMessages[index+1].hasTail = true
                    }
                }
            } else if index == self.rawMessages.count - 1 {
                newMessage.hasDate = true
            }
            
            // 이전 메시지와 사용자가 다른지 -> 프로필 표시 여부
            if index > 0 {
                let prevMessage = self.rawMessages[index - 1]
                if prevMessage.userId != newMessage.userId {
                    newMessage.hasTail = true
                }
            } else if index == 0 {
                newMessage.hasTail = true
            }
            
            return newMessage
        }
        return message
    }
}

extension ChatRoomViewModel {
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
                self.rawMessages = data.messageList ?? []
                self.output.messages.accept(self.getProcessedMessages())
            })
            .disposed(by: bag)
    }
    
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

            let messageBundleIds: String = rooms[0].room?.messageBundleIds ?? ""
            let bundleId = messageBundleIds.parseMessageBundleIds()?.last ?? ""
            
            return ModelPubChatMessage(roomId: room.id ?? "",
                                       roomType: room.type ?? 0,
                                       roomname: room.roomname ?? "",
                                       userId: data.id ?? -1,
                                       username: data.username ?? "",
                                       messageBundleId: bundleId,
                                       receiverIds: room.members?.map { "\($0.userId ?? -1)" },
                                       type: 0,
                                       content: input.text.value)
        }
        return nil
    }
}
