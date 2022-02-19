//
//  ModelRoom.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation

/// 채팅방 현재 상태
struct ModelRoom: Codable {
    /// Room id
    var id: String?
    
    /// room 이름
    var roomname: String?
    
    /// room img
    var img: String?
    
    /// 0=갠톡, 1=단톡, 2=오픈톡
    var type: Int?
    
    /// 멤버 목록
    var members: [RoomMember]?
    
    /// 메시지 번들 아이디 목록
    /// 가장 최신 메시지는 가장 마지막 id에 할당됨
    var messageBundleIds: String?
    
    /// 공지 id 목록
    var noticeIds: String?
    
    var isPinned: Bool?
    var isSilent: Bool?
}

struct ModelTimeStamp: Codable {
    var timestamp: Int?
    var date: String?
}

/// 채팅방 멤버 모델
struct RoomMember: Codable {
    
    /// 유저 ID
    var userId: Int?
    
    /// 사용자 이름
    var username: String?
    
    /// 문자열 프로필 데이터
    var profile: String?
    
    /// 채팅방 참가 여부
    var joining: Bool?
    
    /// 채팅방 초대된 시간
    var joinedAt: String?
    
    /// 채팅방 잠깐 들어온 시간
    var enteredAt: String?
    
    /// 채팅방 잠깐 나간 시간
    var awayAt: String?
    
    /// 채팅방 영영 나간 시간
    var leftAt: String?
    
    /// 프로필 이미지 URL
    var profileImageURL: String?
    
    /// 배경 이미지 URL
    var bgImageURL: String?
    
    /// 상태 메시지
    var bio: String?
}
