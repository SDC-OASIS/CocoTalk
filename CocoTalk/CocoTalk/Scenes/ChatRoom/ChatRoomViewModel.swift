//
//  ChatRoomViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/19.
//


import Foundation
import RxSwift

protocol ChatRoomInput {
    
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
        
    }
    
    struct Dependency: ChatRoomDependency {
        
    }
    
    struct Output: ChatRoomOutput {
        
    }
}


extension ChatRoomViewModel {
    
}
