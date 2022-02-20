//
//  ChatDrawerViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/19.
//


import Foundation
import RxSwift
import RxRelay

protocol ChatDrawerInput {
    
}

protocol ChatDrawerDependency {
    var roomId: BehaviorRelay<String> { get }
}

protocol ChatDrawerOutput {
    var roomMember: BehaviorRelay<[RoomMember]> { get }
}

class ChatDrawerViewModel {
    
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    init(roomId: String, roomMember: [RoomMember]) {
        dependency.roomId.accept(roomId)
        output.roomMember.accept(roomMember)
    }
    
    struct Input: ChatDrawerInput {
        
    }
    
    struct Dependency: ChatDrawerDependency {
        var roomId = BehaviorRelay<String>(value: "")
    }
    
    struct Output: ChatDrawerOutput {
        var roomMember = BehaviorRelay<[RoomMember]>(value: [])
    }
}


extension ChatDrawerViewModel {
    
}
