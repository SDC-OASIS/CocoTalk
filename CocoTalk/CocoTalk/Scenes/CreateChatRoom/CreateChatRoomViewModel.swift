//
//  CreateChatRoomViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/12.
//

import Foundation
import RxSwift
import RxRelay
import SwiftKeychainWrapper
import UIKit

protocol CreateChatRoomInput {
    var keyword: BehaviorRelay<String> { get }
}

protocol CreateChatRoomDependency {
    var isFailed: BehaviorRelay<Bool?> { get }
    var isLoading: BehaviorRelay<Bool> { get }
    var selectableFriends: BehaviorRelay<[SelectableProfile]> { get }
    var filteredSelectableFriends: BehaviorRelay<[SelectableProfile]> { get }
    var selectedFriends: [SelectableProfile] { get }
}

protocol CreateChatRoomOutput {
    var isRoomExist: BehaviorRelay<Bool?> { get }
}

class CreateChatRoomViewModel {
    
    var chatRoomRepository = ChatRoomRepository()
    var userRepository = UserRepository()
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    init() {
        let selectableProfiles = convertToSelectableProfile(UserRepository.items)
        dependency.selectableFriends.accept(selectableProfiles)
        dependency.filteredSelectableFriends.accept(selectableProfiles)
    }
    
    struct Input: CreateChatRoomInput {
        var keyword = BehaviorRelay<String>(value: "")
    }
    
    #warning("웹소켓 헬퍼 추가")
    struct Dependency: CreateChatRoomDependency {
        var isFailed = BehaviorRelay<Bool?>(value: nil)
        var isLoading = BehaviorRelay<Bool>(value: false)
        var selectableFriends = BehaviorRelay<[SelectableProfile]>(value: [])
        var filteredSelectableFriends = BehaviorRelay<[SelectableProfile]>(value: [])
        var selectedFriends: [SelectableProfile] {
            get {
                return selectableFriends.value.filter { $0.isSelected }
            }
        }
    }
    
    struct Output: CreateChatRoomOutput {
        var isRoomExist = BehaviorRelay<Bool?>(value: nil)
    }
    
    func convertToSelectableProfile(_ profiles: [ModelProfile]) -> [SelectableProfile] {
        return profiles.map {
            SelectableProfile(id: $0.id ?? -1,
                              profileImageUrl: $0.profileImageURL ?? "",
                              profile: $0.profile ?? "",
                              username: $0.username ?? "",
                              isSelected: false)
        }
    }
}


extension CreateChatRoomViewModel {
    /// 개인톡방 생성
    func checkChatRoomExist() {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        
        let userId = dependency.selectedFriends.first?.id.description
        
        dependency.isLoading.accept(true)
        chatRoomRepository.checkRoomExist(with: token, memberId: userId ?? "")
            .subscribe(onNext: { [weak self] response in
                guard let self = self else {
                    return
                }
                
                guard let room = response.data else {
                    self.dependency.isFailed.accept(true)
                    return
                }
                
                if let _ = room.id {
                    self.dependency.isLoading.accept(false)
                    self.output.isRoomExist.accept(true)
                } else {
                    self.createChatRoom()
                }
                
            }).disposed(by: bag)
    }
    
    func createChatRoom() {
//        let token: String? = KeychainWrapper.standard[.accessToken]
//        guard let token = token else {
//            return
//        }
        guard let myProfile = UserDefaults.getMyData() else {
            dependency.isFailed.accept(true)
            return
        }
        
        var selectedMembers = dependency.selectedFriends
        let roomName = selectedMembers.first?.username
        selectedMembers.append(SelectableProfile(id: myProfile.id ?? -1,
                                                 profileImageUrl: "",
                                                 profile: myProfile.profile ?? "",
                                                 username: myProfile.username ?? "",
                                                 isSelected: true))
        let members = selectedMembers.map {
            return UserWithPlainStringProfile(userId: $0.id, username: $0.username, profile: $0.profile)
        }
        
        
        let data = ModelCreateChatRoomRequest(roomname: roomName, img: "", type: 0, members: members)
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        appDelegate.listSocket?.createRoom(data)
        dependency.isFailed.accept(false)
    }
    
    func findFriendByKeyword() {
        let newValue: [SelectableProfile]
        if input.keyword.value.isEmpty {
            newValue = dependency.selectableFriends.value
        } else {
            newValue = dependency.selectableFriends.value.filter { $0.username.contains(input.keyword.value) }
        }
        dependency.filteredSelectableFriends.accept(newValue)
    }
    
    func selectFriend(_ index: Int) {
        let selectedValue = dependency.filteredSelectableFriends.value[index]
        var newValue = dependency.selectableFriends.value
        if let idx = newValue.firstIndex(where: { $0.id == selectedValue.id }) {
            newValue[idx].isSelected.toggle()
        }
        dependency.selectableFriends.accept(newValue)
        findFriendByKeyword()
    }
    
    func deselectFriend(_ index: Int) {
        let friend = dependency.selectedFriends[index]
        var newValue = dependency.selectableFriends.value
        
        if let idx = newValue.firstIndex(where: { $0.id == friend.id }) {
            newValue[idx].isSelected = false
        }
        dependency.selectableFriends.accept(newValue)
        findFriendByKeyword()
    }
}

/// SelectableProfile
/// 채팅방 생성시 사용되는 프로필
struct SelectableProfile {
    var id: Int
    var profileImageUrl: String?
    var profile: String
    var username: String
    var isSelected: Bool
}
