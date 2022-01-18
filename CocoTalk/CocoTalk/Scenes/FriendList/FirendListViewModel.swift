//
//  FirendListViewModel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/17.
//

import Foundation
import RxSwift

protocol FriendListInput {
    var profileList: [ModelProfile] { get set }
}

protocol FriendListDependency {
    var myProfile: ModelProfile { get set }
    var favoriteProfile: [ModelProfile] { get set }
    var sections: [String] { get set }
}

protocol FriendListOutput {
    
}

class FriendListViewModel {
    
    var bag = DisposeBag()
    var input = Input()
    var dependency = Dependency()
    var output = Output()
    
    struct Input: FriendListInput {
        var profileList: [ModelProfile] = []
    }
    
    struct Dependency: FriendListDependency {
        var myProfile: ModelProfile = ModelProfile(id: -1, "병학", bio: "✌️", profileImageUrl: "")
        var favoriteProfile: [ModelProfile] = []
        var sections = ["내 프로필", "즐겨찾기", "친구 목록"]
    }
    
    struct Output: FriendListOutput {
        
    }
    
    init() {
        for _ in 0..<40 {
            input.profileList.append(ModelProfile.createRandomProfile())
        }
    }
}


extension FriendListViewModel {
    
}
