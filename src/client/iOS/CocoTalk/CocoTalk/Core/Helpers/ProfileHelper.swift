//
//  ProfileHelper.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/13.
//

import Foundation

struct ProfileHelper {
    
    func decodeProfile(profile: ModelProfile) -> ModelProfile {
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
    
    func decodeProfile(profile: RoomMember) -> RoomMember {
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
