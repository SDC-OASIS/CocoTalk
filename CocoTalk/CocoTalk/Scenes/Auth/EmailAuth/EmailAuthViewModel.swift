//
//  EmailAuthViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/07.
//

import Foundation
import RxSwift
import RxRelay
import SwiftKeychainWrapper
import FirebaseMessaging

protocol EmailAuthInput {
    var code: BehaviorRelay<String> { get }
}

protocol EmailAuthDependency {
    var signupData: ModelSignupData? { get set }
    var isAuthenticated: BehaviorRelay<Bool> { get }
    var error: BehaviorRelay<EmailAuthError?> { get }
    var isLoadingSignin: BehaviorRelay<Bool> { get }
    var isLoadingSignup: BehaviorRelay<Bool> { get }
    var isLoadingVerify: BehaviorRelay<Bool> { get }
    var isSignupComplete: BehaviorRelay<Bool> { get }
    var isSigninComplete: BehaviorRelay<Bool> { get }
    var isValidEmail: BehaviorRelay<Bool?> { get }
    var isLoading: Bool { get }
    var FCMtoken: String { get set }
}

protocol EmailAuthOutput {
    var signupResult: ModelSignupResponse? { get set }
}

class EmailAuthViewModel {
    
    var authRepository = AuthRepository()
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    init() {
        let token: String? = KeychainWrapper.standard[.fcmToken]
        if token == nil {
            Messaging.messaging().token { token, error in
                if let error = error {
                    print("Error fetching FCM registration token: \(error)")
                } else if let token = token {
                    KeychainWrapper.standard[.fcmToken] = token
                }
            }
        } else {
            dependency.FCMtoken = token!
        }
    }
    
    struct Input: EmailAuthInput {
        var code = BehaviorRelay<String>(value: "")
    }
    
    struct Dependency: EmailAuthDependency {
        var signupData: ModelSignupData?
        var error = BehaviorRelay<EmailAuthError?>(value: nil)
        
        var isAuthenticated = BehaviorRelay<Bool>(value: false)
        var isValidEmail = BehaviorRelay<Bool?>(value: nil)
        var isSignupComplete = BehaviorRelay<Bool>(value: false)
        var isSigninComplete = BehaviorRelay<Bool>(value: false)
        var isSignedIn: Bool {
            get {
                let token: String? = KeychainWrapper.standard[.accessToken]
                return token != nil
            }
        }
        
        var isLoadingSignin = BehaviorRelay<Bool>(value: false)
        var isLoadingSignup = BehaviorRelay<Bool>(value: false)
        var isLoadingVerify = BehaviorRelay<Bool>(value: false)
        var isLoading: Bool {
            get {
                return isLoadingVerify.value || isLoadingSignup.value || isLoadingSignin.value
            }
        }
        var FCMtoken = ""
    }
    
    struct Output: EmailAuthOutput {
        var signupResult: ModelSignupResponse?
    }
    
}


extension EmailAuthViewModel {
    func signin(data: ModelSignupData) {
        dependency.isLoadingSignin.accept(true)
        dependency.isSigninComplete.accept(false)
        authRepository.signin(cid: data.cid, password: data.password)
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
    
    func postFCMToken() {
        guard let userId = output.signupResult?.id else {
                  return
              }
        print("FCM: \(dependency.FCMtoken)")
        print("USERID: \(userId)")
        authRepository.postFCMToken(dependency.FCMtoken, userId: userId)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self,
                      let data = self.dependency.signupData else {
                          return
                      }
                self.signin(data: data)
            }).disposed(by: bag)
    }
    
    func signup(data: ModelSignupData) {
        dependency.isLoadingSignup.accept(true)
        dependency.isSignupComplete.accept(false)
        authRepository.signup(with: data)
            .subscribe(onNext: { [weak self] response in
                self?.dependency.isLoadingSignup.accept(false)
                guard let self = self,
                      let isSuccess = response.isSuccess else {
                          return
                      }
                if isSuccess {
                    guard let result = response.result else {
                        return
                    }
                    if let data = try? JSONEncoder().encode(result) {
                        UserDefaults.standard.set(data, forKey: UserDefaultsKey.myData.rawValue)
                    }
                    self.dependency.isSignupComplete.accept(true)
                    self.output.signupResult = result
                    self.postFCMToken()
                } else {
                    self.dependency.isSignupComplete.accept(false)
                }
            }).disposed(by: bag)
    }
    
    func verifyEmail(with email: String) {
        dependency.isLoadingVerify.accept(true)
        authRepository.verifyEmail(email: email, code: input.code.value)
            .subscribe(onNext: { [weak self] result in
                self?.dependency.isLoadingVerify.accept(false)
                guard let self = self,
                      let result = result.result else {
                          return
                      }
                self.dependency.isValidEmail.accept(result.isValid)
                if result.isValid {
                    guard let data = self.dependency.signupData else {
                        return
                    }
                    self.signup(data: data)
                }
            }).disposed(by: bag)
    }
}

// MARK: - EmailAuthError
enum EmailAuthError: Error {
    case invalidCode
}
