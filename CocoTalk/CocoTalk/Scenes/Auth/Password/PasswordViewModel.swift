//
//  PasswordViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/06.
//

import Foundation
import RxSwift
import RxRelay

protocol PasswordInput {
    var idText: BehaviorRelay<String> { get }
}

protocol PasswordDependency {
    var error: BehaviorRelay<PasswordViewModelError?> { get }
    var validId: BehaviorRelay<Bool?> { get }
    var isLoading: BehaviorRelay<Bool> { get }
}

protocol PasswordOutput {
    
}

class PasswordViewModel {
    
    var authRepository = AuthRepository()
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    struct Input: PasswordInput {
        var idText = BehaviorRelay<String>(value: "")
    }
    
    struct Dependency: PasswordDependency {
        var error = BehaviorRelay<PasswordViewModelError?>(value: nil)
        var validId = BehaviorRelay<Bool?>(value: nil)
        var isLoading = BehaviorRelay<Bool>(value: false)
    }
    
    struct Output: PasswordOutput {
        
    }
}


extension PasswordViewModel {
    
    func checkIdDuplicated() {
        dependency.isLoading.accept(true)
        authRepository.isIdExist(id: "id")
            .subscribe(onNext: { [weak self] result in
                self?.dependency.isLoading.accept(false)
                guard let self = self,
                      let result = result.data  else {
                    return
                }
                if let _ = result.id {
                    self.dependency.error.accept(.existUser)
                    self.dependency.validId.accept(false)
                } else {
                    self.dependency.validId.accept(true)
                }
            }).disposed(by: bag)
    }
}


enum PasswordViewModelError: Error {
    case existUser
}
