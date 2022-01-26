//
//  ChatRoomViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/19.
//


import Foundation
import RxSwift

protocol ChatRoomInput {
    var messages: [ModelMessage] { get set }
}

protocol ChatRoomDependency {
    
}

protocol ChatRoomOutput {
    
}

class ChatRoomViewModel {
    
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    struct Input: ChatRoomInput {
        var messages: [ModelMessage] = []
    }
    
    struct Dependency: ChatRoomDependency {
        
    }
    
    struct Output: ChatRoomOutput {
        
    }
}


extension ChatRoomViewModel {
    
    func getMessages() {
        for i in 0..<40 {
            input.messages.append(ModelMessage.getRandomMessage(id: i))
        }
    }
}
