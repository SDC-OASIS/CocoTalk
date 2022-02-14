//
//  ChatRoomListViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/18.
//


import Foundation
import RxSwift
import SwiftKeychainWrapper
import RxRelay

protocol ChatRoomListInput {

}

protocol ChatRoomListDependency {
    var listSocket: BehaviorRelay<WebSocketHelper?> { get }
}

protocol ChatRoomListOutput {
    var rooms: BehaviorRelay<[ModelRoomList]> { get }
}

class ChatRoomListViewModel {
    
    var chatRoomRepository = ChatRoomRepository()
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    struct Input: ChatRoomListInput {
        
    }
    
    struct Dependency: ChatRoomListDependency {
        var listSocket = BehaviorRelay<WebSocketHelper?>(value: nil)
    }
    
    struct Output: ChatRoomListOutput {
        var rooms = BehaviorRelay<[ModelRoomList]>(value: [])
    }
}


extension ChatRoomListViewModel {
    #warning("삭제")
    /*
         1. 채팅방 입장시 room 정보 전달
         1-1. UserRepository, RoomRepository item 저장된 상태 (ChatRepository은 저장되는대로 ㄲ)
         2. 채팅방에서 룸정보 기반으로 (유저, 채팅방 이름, 메시지 목록) 불러오기
     */
    
    func fetch() {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        chatRoomRepository.fetchFromServer(with: token)
            .subscribe(onNext: { [weak self] response in
                guard let self = self,
                      let rooms = response.data else {
                    return
                }
                ChatRoomRepository.items = rooms
                self.output.rooms.accept(ChatRoomRepository.chatRooms)
            }).disposed(by: bag)
    }
}



