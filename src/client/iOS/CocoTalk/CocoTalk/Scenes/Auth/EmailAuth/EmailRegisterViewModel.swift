//
//  EmailRegisterViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/07.
//


import Foundation
import RxSwift
import RxRelay

protocol EmailRegisterInput {
    var email: BehaviorRelay<String> { get }
}

protocol EmailRegisterDependency {
    var error: BehaviorRelay<EmailRegisterError?> { get }
    var validEmail: BehaviorRelay<Bool?> { get }
    var requestSuccess: BehaviorRelay<Bool?> { get }
    var isLoadingCheckEmail: BehaviorRelay<Bool> { get }
    var isLoadingRequestCode: BehaviorRelay<Bool> { get }
}

protocol EmailRegisterOutput {
    
}

class EmailRegisterViewModel {
    
    var authRepository = AuthRepository()
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    struct Input: EmailRegisterInput {
        var email = BehaviorRelay<String>(value: "")
    }
    
    struct Dependency: EmailRegisterDependency {
        var error = BehaviorRelay<EmailRegisterError?>(value: nil)
        var validEmail = BehaviorRelay<Bool?>(value: nil)
        var requestSuccess = BehaviorRelay<Bool?>(value: nil)
        var isLoadingCheckEmail = BehaviorRelay<Bool>(value: false)
        var isLoadingRequestCode = BehaviorRelay<Bool>(value: false)
    }
    
    struct Output: EmailRegisterOutput {
        
    }
}


extension EmailRegisterViewModel {
    
    func checkEmailDuplicated() {
        dependency.isLoadingCheckEmail.accept(true)
        authRepository.isEmailExist(email: input.email.value)
            .subscribe(onNext: { [weak self] result in
                self?.dependency.isLoadingCheckEmail.accept(false)
                guard let self = self else {
                    return
                }
                
                if let data = result.data,
                   let _ = data.id{
                    self.dependency.error.accept(.existUser)
                    self.dependency.validEmail.accept(false)
                } else {
                    self.dependency.validEmail.accept(true)
                }
            }).disposed(by: bag)
    }
    
    func requestEmailAuthenticationCode() {
        dependency.isLoadingRequestCode.accept(true)
        authRepository.requestEmailAuthenticationCode(with: input.email.value)
            .subscribe(onNext: { [weak self] result in
                self?.dependency.isLoadingRequestCode.accept(false)
                guard let self = self,
                      let _ = result.result else {
                    return
                }
                self.dependency.requestSuccess.accept(true)
            }).disposed(by: bag)
    }
}

enum EmailRegisterError: Error {
    case existUser
}
