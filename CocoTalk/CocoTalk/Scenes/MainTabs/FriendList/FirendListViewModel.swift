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
    var isFailed: BehaviorRelay<Bool?> { get }
    var isLoading: BehaviorRelay<Bool> { get }
    var favoriteProfile: [ModelProfile] { get set }
    var sections: [String] { get set }
}

protocol FriendListOutput {
    var isRoomExist: BehaviorRelay<Bool?> { get }
    var roomId: BehaviorRelay<String?> { get }
    var talkMembers: BehaviorRelay<[RoomMember]?> { get }
    var myProfile: BehaviorRelay<ModelProfile> { get }
    var friends: BehaviorRelay<[ModelProfile]> { get }
}

class FriendListViewModel {
    
    var chatRoomRepository = ChatRoomRepository()
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
        var isFailed = BehaviorRelay<Bool?>(value: nil)
        var isLoading = BehaviorRelay<Bool>(value: false)
        var favoriteProfile: [ModelProfile] = []
        var sections = ["내 프로필", "즐겨찾기", "친구 목록"]
    }
    
    struct Output: FriendListOutput {
        var talkMembers = BehaviorRelay<[RoomMember]?>(value: nil)
        var roomId = BehaviorRelay<String?>(value: nil)
        var isRoomExist = BehaviorRelay<Bool?>(value: nil)
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
    
    #warning("채팅방 생성하는 로직 모달로 이동시키기")
    func checkChatRoomExist(userId: Int) {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        
        dependency.isLoading.accept(true)
        chatRoomRepository.checkRoomExist(with: token, memberId: "\(userId)")
            .subscribe(onNext: { [weak self] response in
                guard let self = self else {
                    return
                }
                
                guard let room = response.data else {
                    self.dependency.isFailed.accept(true)
                    return
                }
                
                self.output.talkMembers.accept(room.members)
                if let id = room.id {
                    self.dependency.isLoading.accept(false)
                    self.output.roomId.accept(id)
                    self.output.isRoomExist.accept(true)
                } else {
                    self.createChatRoom(userId)
                }
                
            }).disposed(by: bag)
    }
    
    func createChatRoom(_ memberId: Int) {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        guard let myProfile = UserDefaults.getMyData(),
              let member = userRepsository.findUserById(memberId) else {
            dependency.isFailed.accept(true)
            return
        }
        
        var selectedMembers: [SelectableProfile] = []
        
        selectedMembers.append(SelectableProfile(id: member.id ?? -1,
                                                 profileImageUrl: "",
                                                 profile: member.profile ?? "",
                                                 username: member.username ?? "",
                                                 isSelected: true))
        selectedMembers.append(SelectableProfile(id: myProfile.id ?? -1,
                                                 profileImageUrl: "",
                                                 profile: myProfile.profile ?? "",
                                                 username: myProfile.username ?? "",
                                                 isSelected: true))
        let members = selectedMembers.map {
            return ProfileForCreateChatRoom(userId: $0.id, username: $0.username, profile: $0.profile)
        }
        let roomName = String(members.reduce("", { $0 + ", \($1.username ?? "")" }).dropFirst(2))
        
        let data = ModelCreateChatRoomRequest(roomname: roomName, img: "", type: 0, members: members)
        
        dependency.isLoading.accept(true)
        chatRoomRepository.createChatRoom(with: token, data: data)
            .subscribe(onNext: { [weak self] response in
                self?.dependency.isLoading.accept(false)
                guard let self = self else {
                    return
                }
                
                guard let room = response.data,
                      let id = room.id else {
                    self.dependency.isFailed.accept(true)
                    return
                }
                
                self.dependency.isFailed.accept(false)
                self.output.isRoomExist.accept(true)
                self.output.roomId.accept(id)
            }).disposed(by: bag)
    }
    
    func addFirendsWithContact() {
#warning("가입되지 않은 연락처는 user.id가 null")
    }
}

struct ModelProfileData: Codable {
    var profile: String?
    var background: String?
    var message: String?
}
