//
//  SmsAuthViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/03.
//


import Foundation
import RxSwift
import RxRelay

protocol SmsAuthInput {
    
}

protocol SmsAuthDependency {
    var countDown: BehaviorRelay<Int> { get }
    var timer: Timer? { get set }
}

protocol SmsAuthOutput {
    
}

class SmsAuthViewModel {
    
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    struct Input: SmsAuthInput {
        
    }
    
    struct Dependency: SmsAuthDependency {
        var countDown = BehaviorRelay<Int>(value: 180)
        var timer: Timer? = nil
    }
    
    struct Output: SmsAuthOutput {
        
    }
}


extension SmsAuthViewModel {
    func startCountDown() {
        
    }
}
