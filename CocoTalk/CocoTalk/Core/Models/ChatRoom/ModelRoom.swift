//
//  ModelRoom.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation

struct ModelRoom: Codable {
    var id: String?
    var roomname: String?
    var img: String?
    /// 0=갠톡, 1=단톡, 2=오픈톡
    var type: Int?
    var members: [RoomMember]?
    var messageBundleIds: String?
    var noticeIds: String?
    
    var isPinned: Bool?
    var isSilent: Bool?
}

struct RoomMember: Codable {
    var userId: Int?
    var username: String?
    var profile: String?
    var joining: Bool?
    var joinedAt: String?
    var enteredAt: String?
    var awayAt: String?
    var leftAt: String?
    
}
