//
//  ModelCreateChatRoomRequest.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/13.
//

import Foundation

struct ModelCreateChatRoomRequest: Codable {
    var roomname: String?
    var img: String?
    var type: Int?
    var members: [UserWithPlainStringProfile]?
}

/// 단순 문자열의 프로필을 포함하는 프로필
struct UserWithPlainStringProfile: Codable {
    var userId: Int?
    var username: String?
    var profile: String?
}

/// 문자열로 들어오는 프로필을 변환하기 위한 구조체
struct PlainStringProfile: Codable {
    /// 프로필 이미지 URL
    var profile: String?
    
    /// 배경 이미지 URL
    var background: String?
    
    /// 상태 메시지
    var message: String?
    
}
