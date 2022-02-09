//
//  SplashViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/08.
//

import Foundation
import RxSwift
import RxRelay
import SwiftKeychainWrapper

protocol SplashInput {
    
}

protocol SplashDependency {
    var isValidToken: BehaviorRelay<Bool?> { get }
    var isSignedIn: Bool { get }
}

protocol SplashOutput {
    
}

class SplashViewModel {
    
    var authRepository = AuthRepository()
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    struct Input: SplashInput {
        
    }
    
    struct Dependency: SplashDependency {
        var isValidToken = BehaviorRelay<Bool?>(value: nil)
        var isSignedIn: Bool {
            get {
                let token: String? = KeychainWrapper.standard[.accessToken]
                return token != nil
            }
        }
    }
    
    struct Output: SplashOutput {
        
    }
}


extension SplashViewModel {
    func verifyToken() {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        authRepository.verifyToken(token)
            .subscribe(onNext: { [weak self] response in
                guard let self = self else {
                    return
                }
                #warning("토큰이 만료되었을 때 로직이 따로 필요하다")
                guard let status = response.status,
                      (200...299).contains(status) else {
                          self.dependency.isValidToken.accept(false)
                          return
                      }
                guard let result = response.result else {
                    return
                }
                self.dependency.isValidToken.accept(result.isValid)
            }).disposed(by: bag)
    }
}
