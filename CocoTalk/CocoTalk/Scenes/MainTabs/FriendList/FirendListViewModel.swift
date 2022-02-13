//
//  FirendListViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/17.
//

import Foundation
import RxSwift
import RxRelay
import SwiftKeychainWrapper

protocol FriendListInput {
    
}

protocol FriendListDependency {
    var favoriteProfile: [ModelProfile] { get set }
    var sections: [String] { get set }
}

protocol FriendListOutput {
    var myProfile: BehaviorRelay<ModelProfile> { get }
    var friends: BehaviorRelay<[ModelProfile]> { get }
}

class FriendListViewModel {
    
    var userRepsository = UserRepository()
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    init() {
        let myProfile = userRepsository.fetchMyProfile()
        output.myProfile.accept(myProfile)
        output.friends.accept(userRepsository.initFetch())
    }
    
    struct Input: FriendListInput {
        
    }
    
    struct Dependency: FriendListDependency {
        var favoriteProfile: [ModelProfile] = []
        var sections = ["내 프로필", "즐겨찾기", "친구 목록"]
    }
    
    struct Output: FriendListOutput {
        var myProfile = BehaviorRelay<ModelProfile>(value: ModelProfile())
        var friends = BehaviorRelay<[ModelProfile]>(value: [])
    }
    
    // MARK: - Helper
    /// handleFriends
    /// JSON string으로 전달된 profile을 디코딩한다.
    func handleFriends(_ friends: [ModelFriend]) -> [ModelProfile] {
        return friends
            .filter {
                if let _ = $0.friend {
                    return true
                } else {
                    return false
                }
            }
            .filter{ $0.type == 0 }
            .map { ProfileHelper().decodeProfile(profile: $0.friend!) }
            .sorted(by: { $0.username ?? "" < $1.username ?? "" })
    }
}


extension FriendListViewModel {
// MARK: - Public methods
    func fetch() {
        getMyProfile()
        getFriends()
    }

    func getMyProfile() {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        userRepsository.fetchMyProfileFromServer(with: token)
            .subscribe(onNext: { [weak self] response in
                guard let self = self,
                      let myProfile = response else {
                    return
                }
                self.output.myProfile.accept(ProfileHelper().decodeProfile(profile: myProfile))
            }).disposed(by: bag)
    }
    
    func getFriends() {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        userRepsository.fetchFromServer(with: token)
            .subscribe(onNext: { [weak self] response in
                guard let self = self,
                      let friends = response else {
                    return
                }
                let profiles = self.handleFriends(friends)
                self.output.friends.accept(profiles)
                UserRepository.items = profiles
            }).disposed(by: bag)
        #warning("비교 후 업데이트된 프로필 표시하기")
    }
    
    func addFirendsWithContact() {
#warning("가입되지 않은 연락처는 user.id가 null")
    }
    
#warning("코코톡 ID로 친구추가 리스트로")
    func addFriendsWithCid() {
        
    }
}

struct ModelProfileData: Codable {
    var profile: String?
    var background: String?
    var message: String?
}
