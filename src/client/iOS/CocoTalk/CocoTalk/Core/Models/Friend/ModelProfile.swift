//
//  ModelProfile.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/17.
//

import Foundation

struct ModelProfile: Codable {
    var id: Int?
    var cid: String?
    var username: String?
    var nickname: String?
    var birth: String?
    var profile: String?
    var phone: String?
    var email: String?
    var loggedinAt: String?
    var status: Int?
    
    var bio: String?
    var profileImageURL: String?
    var bgImageURL: String?
    
    #warning("CoreData로 확인하기")
    /// CoreData에 업데이트가 생겼을 경우
    /// 1. 프로필 수정 시간 최신화
    /// 2. 뉴 프로필 true (24시간 안에 봤을 경우)
    var isNewProfile: Bool?
}
