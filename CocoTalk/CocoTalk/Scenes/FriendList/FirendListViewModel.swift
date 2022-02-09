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
        
        var friends = userRepsository.initFetch()
        friends = handleFriends(friends)
        output.friends.accept(friends)
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
    func handleFriends(_ friends: [ModelProfile]) -> [ModelProfile] {
        return friends.map { decodeProfile($0) }
    }
    
    func decodeProfile(_ profile: ModelProfile) -> ModelProfile {
        let decoder = JSONDecoder()
        guard let jsonString = profile.profile else {
            return profile
        }
        var profileData = profile
        let jsonData = Data(jsonString.utf8)
        let decoded = try? decoder.decode(ModelProfileData.self, from: jsonData)
        profileData.profileImageURL = decoded?.profile
        profileData.bgImageURL = decoded?.background
        profileData.bio = decoded?.message
        return profileData
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
                self.output.myProfile.accept(self.decodeProfile(myProfile))
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
                      var friends = response else {
                    return
                }
                friends = self.handleFriends(friends)
                self.output.friends.accept(friends)
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
