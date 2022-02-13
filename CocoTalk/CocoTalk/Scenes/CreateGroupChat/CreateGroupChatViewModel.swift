//
//  CreateGroupChatViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/13.
//

import Foundation
import RxSwift
import RxRelay
import SwiftKeychainWrapper

protocol CreateGroupChatInput {
    var roomName: BehaviorRelay<String> { get }
}

protocol CreateGroupChatDependency {
    var isLoading: BehaviorRelay<Bool> { get }
    var isFailed: BehaviorRelay<Bool?> { get }
    var selected: BehaviorRelay<[SelectableProfile]> { get }
    var defaultRoomName: String { get }
}

protocol CreateGroupChatOutput {
    
}

class CreateGroupChatViewModel {
    
    var chatRoomRepository = ChatRoomRepository()
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    struct Input: CreateGroupChatInput {
        var roomName = BehaviorRelay<String>(value: "")
    }
    
    struct Dependency: CreateGroupChatDependency {
        var isLoading = BehaviorRelay<Bool>(value: false)
        var isFailed = BehaviorRelay<Bool?>(value: nil)
        var selected = BehaviorRelay<[SelectableProfile]>(value: [])
        var defaultRoomName: String {
            get {
                return String(selected.value.reduce("", { $0 + ", \($1.username)" }).dropFirst(2))
            }
        }
    }
    
    struct Output: CreateGroupChatOutput {
        
    }
}


extension CreateGroupChatViewModel {
    
    func createChatRoom() {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        guard let myProfile = UserDefaults.getMyData() else {
            dependency.isFailed.accept(true)
            return
        }
        
        var selectedMembers = dependency.selected.value
        selectedMembers.append(SelectableProfile(id: myProfile.id ?? -1,
                                                 profileImageUrl: "",
                                                 profile: myProfile.profile ?? "",
                                                 username: myProfile.username ?? "",
                                                 isSelected: true))
        let members = selectedMembers.map {
            return ProfileForCreateChatRoom(userId: $0.id, username: $0.username, profile: $0.profile)
        }
        
        var roomName: String
        if input.roomName.value.isEmpty {
            roomName = dependency.defaultRoomName
        } else {
            roomName = input.roomName.value
        }
        let data = ModelCreateChatRoomRequest(roomname: roomName, img: "", type: 1, members: members)
        
        dependency.isLoading.accept(true)
        chatRoomRepository.createChatRoom(with: token, data: data)
            .subscribe(onNext: { [weak self] response in
                self?.dependency.isLoading.accept(false)
                guard let self = self else {
                    return
                }
                
                guard let _ = response.data else {
                    self.dependency.isFailed.accept(true)
                    return
                }
                self.dependency.isFailed.accept(false)
            }).disposed(by: bag)
    }
}
