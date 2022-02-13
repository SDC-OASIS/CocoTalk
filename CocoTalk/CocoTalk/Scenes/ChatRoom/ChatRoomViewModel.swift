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
    var messages: BehaviorRelay<[ModelMessage]> { get }
    var members: BehaviorRelay<[ModelProfile]> { get }
    var roomInfo: BehaviorRelay<ModelRoom?> { get }
}

protocol ChatRoomOutput {
    
}

class ChatRoomViewModel {
    
    var chatRoomRepository = ChatRoomRepository()
    var messageRepository = MessageRepository()
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    var rooomId: String
    
    init(roomId: String) {
        self.rooomId = roomId
    }
    
    struct Input: ChatRoomInput {
        var type = BehaviorRelay<MessageTypeEnum>(value: .text)
        var text = BehaviorRelay<String>(value: "")
    }
    
    struct Dependency: ChatRoomDependency {
        var isLoading = BehaviorRelay<Bool>(value: false)
        var socket = BehaviorRelay<WebSocketHelper?>(value: nil)
        var messages = BehaviorRelay<[ModelMessage]>(value: [])
        var members = BehaviorRelay<[ModelProfile]>(value: [])
        var roomInfo = BehaviorRelay<ModelRoom?>(value: nil)
        
    }
    
    struct Output: ChatRoomOutput {
        
    }
}


extension ChatRoomViewModel {
    
    func getMessages() {
        var newValue = dependency.messages.value
        for i in 0..<40 {
            newValue.append(ModelMessage.getRandomMessage(id: i))
        }
        dependency.messages.accept(newValue)
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
                self.dependency.roomInfo.accept(room)
            }).disposed(by: bag)
    }
    
    func sendMessage() {
        if input.text.value.isEmpty {
            return
        }
        
        guard let socket = dependency.socket.value,
              let roomInfo = dependency.roomInfo.value else {
            return
        }
        
        if let savedData = UserDefaults.standard.object(forKey: UserDefaultsKey.myData.rawValue) as? Data,
           let data = try? JSONDecoder().decode(ModelSignupResponse.self, from: savedData) {
            
//            let message = ModelChatMessagePub(roomId: self.rooomId,
//                                              roomType: roomInfo.type ?? 0,
//                                              roomname: roomInfo.roomname ?? "",
//                                              userId: data.id ?? -1,
//                                              username: data.username ?? "",
//                                              messageBundleId: "",
//                                              receiverIds: <#T##[String]?#>,
//                                              type: 0,
//                                              content: input.text.value)
//            socket.sendMessage(message)
//            
//            messageRepository.insert(ModelMessage(id: <#T##Int?#>,
//                                                  text: <#T##String?#>,
//                                                  mediaType: <#T##Int?#>,
//                                                  mediaUrls: <#T##[String]?#>,
//                                                  senderId: <#T##Int?#>,
//                                                  date: <#T##Date?#>,
//                                                  isMe: <#T##Bool?#>,
//                                                  hasTail: <#T##Bool?#>))
        }
    }
}
