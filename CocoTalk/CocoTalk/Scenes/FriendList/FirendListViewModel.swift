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
        for i in 0..<40 {
            print(i)
            if i % 2 == 0 {
                input.profileList.append(ModelProfile(id: i, name: UUID().uuidString.split(separator: "-")[0].description, bio: "\(i)호기 입니다. 안녕하세요~~! 😀 솰라솰라솰라솰라솰라솰라솰라솰라솰라솰라", profileImageUrl: ""))
            } else {
                input.profileList.append(ModelProfile(id: i, name: UUID().uuidString.split(separator: "-")[0].description, bio: "", profileImageUrl: ""))
            }
        }
    }
}


extension FriendListViewModel {
    
}
