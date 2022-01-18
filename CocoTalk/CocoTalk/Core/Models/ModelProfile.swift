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
    #warning("UserDefault로 확인하기")
    /// CoreData에 업데이트가 생겼을 경우
    /// 1. 프로필 수정 시간 최신화
    /// 2. 뉴 프로필 true (24시간 안에 봤을 경우)
    var isNewProfile: Bool?
    /// 업데이트한 프로필을 표시하기 위해
    /// 소켓으로 해야되나..? 개발 할때 팀원들 프로필 수정하는거 확인하기
    var updatedTime: Date?
}

extension ModelProfile {
    init(id: Int, _ name: String, bio: String, profileImageUrl: String) {
        self.id = id
        self.name = name
        self.bio = bio
        self.profileImageUrl = profileImageUrl
        self.isNewProfile = true
        self.updatedTime = nil
    }
    
    static func createRandomProfile() -> ModelProfile {
        let _bio = Bool.random() ? "랜덤으로 생성되는 상태메시지 입니다 😀" : ""
        return ModelProfile(id: Int.random(in: 0..<99999),
                            name: UUID().uuidString.split(separator: "-")[0].description,
                            bio: _bio,
                            profileImageUrl: "")
    }
}
