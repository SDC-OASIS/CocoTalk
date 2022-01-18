//
//  ModelProfile.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/17.
//

import Foundation

struct ModelProfile: Codable {
    var id: Int?
    var name: String?
    var bio: String?
    var profileImageUrl: String?
}

extension ModelProfile {
    init(id: Int, _ name: String, bio: String, profileImageUrl: String) {
        self.id = id
        self.name = name
        self.bio = bio
        self.profileImageUrl = profileImageUrl
    }
}
