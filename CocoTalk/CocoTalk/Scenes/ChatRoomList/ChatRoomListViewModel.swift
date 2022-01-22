//
//  ChatRoomListViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/18.
//


import Foundation
import RxSwift

protocol ChatRoomListInput {
    var roomList: [ModelChatRoom] { get set }
}

protocol ChatRoomListDependency {
    
}

protocol ChatRoomListOutput {
    
}

class ChatRoomListViewModel {
    
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    struct Input: ChatRoomListInput {
        var roomList: [ModelChatRoom] = []
    }
    
    struct Dependency: ChatRoomListDependency {
        
    }
    
    struct Output: ChatRoomListOutput {
        
    }
    
    init() {
        for _ in 0..<40 {
            input.roomList.append(ModelChatRoom.createRandom())
        }
    }
}


extension ChatRoomListViewModel {
    
}
