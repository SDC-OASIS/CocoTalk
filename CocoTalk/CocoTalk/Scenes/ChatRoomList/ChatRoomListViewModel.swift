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
        
    }
    
    struct Output: ChatRoomListOutput {
        var rooms = BehaviorRelay<[ModelRoomList]>(value: [])
    }
}


extension ChatRoomListViewModel {
    func fetch() {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        chatRoomRepository.fetchFromServer(with: token)
            .subscribe(onNext: { [weak self] rooms in
                guard let self = self,
                      let rooms = rooms else {
                    return
                }
                self.output.rooms.accept(rooms)
            }).disposed(by: bag)
    }
}
