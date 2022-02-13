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
import FirebaseMessaging

protocol SigninInput {
    var cid: BehaviorRelay<String> { get }
    var password: BehaviorRelay<String> { get }
}

protocol SigninDependency {
    var error: BehaviorRelay<Bool?> { get }
    var isLoadingSignin: BehaviorRelay<Bool> { get }
    var isSigninComplete: BehaviorRelay<Bool> { get }
    var fcmToken: String? { get set }
}

protocol SigninOutput {
    
}

class SigninViewModel {
    
    var userRepository = UserRepository()
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
        var error = BehaviorRelay<Bool?>(value: nil)
        var isLoadingSignin = BehaviorRelay<Bool>(value: false)
        var isSigninComplete = BehaviorRelay<Bool>(value: false)
        var fcmToken: String? = nil
    }
    
    struct Output: SigninOutput {
    }
}

extension SigninViewModel {
#warning("로그인 에러 알리기")
    func signin() {
        let token: String? = KeychainWrapper.standard[.fcmToken]
        if token == nil {
            Messaging.messaging().token { [weak self] token, error in
                if let error = error {
                    print("Error fetching FCM registration token: \(error)")
                } else if let token = token {
                    KeychainWrapper.standard[.fcmToken] = token
                    self?.dependency.fcmToken = token
                }
            }
        } else {
            dependency.fcmToken = token
        }
        
        guard let fcmToken = dependency.fcmToken else {
            dependency.error.accept(true)
            return
        }
        
        dependency.isLoadingSignin.accept(true)
        dependency.isSigninComplete.accept(false)
        authRepository.signin(cid: input.cid.value, password: input.password.value, fcmToken: fcmToken)
            .subscribe(onNext: { [weak self] result in
                guard let self = self else {
                          return
                      }
                
                guard let response = result.result,
                      let accessToken = response.accessToken,
                      let refreshToken = response.refreshToken else {
                          self.dependency.isSigninComplete.accept(false)
                          self.dependency.error.accept(true)
                          return
                      }
                KeychainWrapper.standard[.accessToken] = accessToken
                KeychainWrapper.standard[.refreshToken] = refreshToken
                self.getProfile()
            }).disposed(by: bag)
    }
    
    private func getProfile() {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        userRepository.fetchMyProfileFromServer(with: token)
            .subscribe(onNext: { [weak self] response in
                self?.dependency.isLoadingSignin.accept(false)
                guard let self = self,
                      let myProfile = response else {
                          self?.dependency.isSigninComplete.accept(false)
                          return
                      }
                UserDefaults.standard.set(myProfile.encode() ?? nil, forKey: UserDefaultsKey.myData.rawValue)
                self.dependency.isSigninComplete.accept(true)
            }).disposed(by: bag)
    }
}
