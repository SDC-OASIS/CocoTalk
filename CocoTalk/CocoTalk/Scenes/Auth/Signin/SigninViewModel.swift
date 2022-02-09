//
//  SigninViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation
import RxSwift
import RxRelay
import SwiftKeychainWrapper

protocol SigninInput {
    var cid: BehaviorRelay<String> { get }
    var password: BehaviorRelay<String> { get }
}

protocol SigninDependency {
    var isLoadingSignin: BehaviorRelay<Bool> { get }
    var isSigninComplete: BehaviorRelay<Bool> { get }
}

protocol SigninOutput {
    
}

class SigninViewModel {
    
    var authRepository = AuthRepository()
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    struct Input: SigninInput {
        var cid = BehaviorRelay<String>(value: "")
        var password = BehaviorRelay<String>(value: "")
    }
    
    struct Dependency: SigninDependency {
        var isLoadingSignin = BehaviorRelay<Bool>(value: false)
        var isSigninComplete = BehaviorRelay<Bool>(value: false)
    }
    
    struct Output: SigninOutput {
    }
}

extension SigninViewModel {
    #warning("로그인 에러 알리기")
    func signin() {
        dependency.isLoadingSignin.accept(true)
        dependency.isSigninComplete.accept(false)
        authRepository.signin(cid: input.cid.value, password: input.password.value)
            .subscribe(onNext: { [weak self] result in
                self?.dependency.isLoadingSignin.accept(false)
                guard let self = self,
                      let response = result.result else {
                          return
                      }
                guard let accessToken = response.accessToken,
                      let refreshToken = response.refreshToken else {
                          return
                      }
                KeychainWrapper.standard[.accessToken] = accessToken
                KeychainWrapper.standard[.refreshToken] = refreshToken
                self.dependency.isSigninComplete.accept(true)
            }).disposed(by: bag)
    }
}
