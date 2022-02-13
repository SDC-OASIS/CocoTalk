//
//  ChatRoomViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/19.
//


import Foundation
import RxSwift
import RxRelay

protocol ChatRoomInput {
    var text: BehaviorRelay<String> { get }
    var type: BehaviorRelay<MessageTypeEnum> { get }
}

protocol ChatRoomDependency {
    var messages: BehaviorRelay<[ModelMessage]> { get }
    var members: BehaviorRelay<[ModelProfile]> { get }
}

protocol ChatRoomOutput {
    
}

class ChatRoomViewModel {
    
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    struct Input: ChatRoomInput {
        var type = BehaviorRelay<MessageTypeEnum>(value: .text)
        var text = BehaviorRelay<String>(value: "")
    }
    
    struct Dependency: ChatRoomDependency {
        var messages = BehaviorRelay<[ModelMessage]>(value: [])
        var members = BehaviorRelay<[ModelProfile]>(value: [])
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
}
