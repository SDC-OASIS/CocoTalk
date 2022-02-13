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
    var members: [ProfileForCreateChatRoom]?
}

struct ProfileForCreateChatRoom: Codable {
    var userId: Int?
    var username: String?
    var profile: String?
}
