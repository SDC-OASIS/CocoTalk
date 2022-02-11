//
//  AddFriendViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/12.
//


import Foundation
import RxSwift
import RxRelay
import SwiftKeychainWrapper

protocol AddFriendInput {
    var cid: BehaviorRelay<String> { get }
}

protocol AddFriendDependency {
    var myCid: BehaviorRelay<String?> { get }
    var isLoading: BehaviorRelay<Bool> { get }
    var isAddCompleted: BehaviorRelay<Bool?> { get }
    var noResult: BehaviorRelay<Bool?> { get }
}

protocol AddFriendOutput {
    var resultProfile: BehaviorRelay<ModelProfile?> { get }
}

class AddFriendViewModel {
    
    var userRepository = UserRepository()
    var authRepository = AuthRepository()
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    init() {
        if let savedData = UserDefaults.standard.object(forKey: UserDefaultsKey.myData.rawValue) as? Data,
           let myData = try? JSONDecoder().decode(ModelSignupResponse.self, from: savedData) {
            dependency.myCid.accept(myData.cid)
        }
    }
    
    struct Input: AddFriendInput {
        var cid = BehaviorRelay<String>(value: "")
    }
    
    struct Dependency: AddFriendDependency {
        var myCid = BehaviorRelay<String?>(value: "")
        var isLoading = BehaviorRelay<Bool>(value: false)
        var isAddCompleted = BehaviorRelay<Bool?>(value: nil)
        var noResult = BehaviorRelay<Bool?>(value: nil)
    }
    
    struct Output: AddFriendOutput {
        var resultProfile = BehaviorRelay<ModelProfile?>(value: nil)
    }
}


extension AddFriendViewModel {
    func findUserByCid() {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        dependency.isLoading.accept(true)
        userRepository.findUserByCid(input.cid.value, token: token)
            .subscribe(onNext: { [weak self] response in
                self?.dependency.isLoading.accept(false)
                guard let self = self else {
                          return
                      }
                guard let response = response,
                      let _ = response.id else {
                          self.dependency.noResult.accept(true)
                          return
                      }
                self.dependency.noResult.accept(false)
                self.output.resultProfile.accept(response.decodeProfile())
            }).disposed(by: bag)
    }
    
    func addFriend() {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token,
              let profile = output.resultProfile.value,
              let id = profile.id else {
            return
        }
        dependency.isLoading.accept(true)
        userRepository.addFriend(id, token: token)
            .subscribe(onNext: { [weak self] response in
                self?.dependency.isLoading.accept(false)
                guard let self = self,
                      let response = response else {
                          return
                      }
                guard let profile = response.toUser else {
                          self.dependency.noResult.accept(true)
                          return
                      }
                self.output.resultProfile.accept(profile.decodeProfile())
                self.dependency.isAddCompleted.accept(true)
            }).disposed(by: bag)
    }
}
