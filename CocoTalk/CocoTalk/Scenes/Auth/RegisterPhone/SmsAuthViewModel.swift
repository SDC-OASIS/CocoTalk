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
    var smsAuthCode: BehaviorRelay<String> { get }
}

protocol SmsAuthDependency {
    var countDown: BehaviorRelay<Int> { get }
    var timer: Timer? { get set }
    var error: BehaviorRelay<SmsAuthError?> { get }
    var isPhoneExist: BehaviorRelay<Bool?> { get }
    var isLoading: BehaviorRelay<Bool?> { get }
    var phoneNumber: String { get set }
}

protocol SmsAuthOutput {
    
}

class SmsAuthViewModel {
    
    var authRepository = AuthRepository()
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    struct Input: SmsAuthInput {
        var smsAuthCode = BehaviorRelay<String>(value: "")
    }
    
    struct Dependency: SmsAuthDependency {
        var countDown = BehaviorRelay<Int>(value: 180)
        var timer: Timer? = nil
        var error = BehaviorRelay<SmsAuthError?>(value: nil)
        var isPhoneExist = BehaviorRelay<Bool?>(value: nil)
        var isLoading = BehaviorRelay<Bool?>(value: false)
        var phoneNumber = ""
    }
    
    struct Output: SmsAuthOutput {
        
    }
}


extension SmsAuthViewModel {
    
    #warning("0")
    func startCountDown() {
        
    }
    
    #warning("1) sms 인증 완료 ")
    func verifyPhoneNumber(data: ModelSignupData) {

    }
    
    #warning("2")
    func checkPhoneExist() {
        dependency.isLoading.accept(true)
        authRepository.isPhoneExist(phone: dependency.phoneNumber)
            .subscribe(onNext: { [weak self] result in
                self?.dependency.isLoading.accept(true)
                guard let self = self,
                      let result = result.data  else {
                          return
                      }
                if result.count == 1,
                   let _ = result[0].id {
                    self.dependency.error.accept(.existPhone)
                    self.dependency.isPhoneExist.accept(true)
                } else {
                    self.dependency.isPhoneExist.accept(false)
                }
            }).disposed(by: bag)
    }
}

enum SmsAuthError: Error {
    case existPhone
    case failedToAuthenticate
}
