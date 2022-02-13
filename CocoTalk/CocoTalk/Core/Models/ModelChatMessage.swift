//
//  ModelChatMessage.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation

struct ModelChatMessageSub: Codable {
    var id: String?
    var roomId: String?
    var messageBundleId: String?
    var userId: Int?
    var content: String?
    var type: Int?
    var sentAt: String?
}

struct ModelChatMessagePub: Codable {
    #warning("삭제")
//    var id: String?
    var roomId: String?
    var roomType: Int?
    var roomname: String?
    var userId: Int?
    var username: String?
    var messageBundleId: String?
    var receiverIds: [String]?
    var type: Int?
    var content: String?
}

struct ModelMessageBundle: Codable {
    var currentMessageBundleCount: Int?
    var currentMessageBundleId: String?
    var nextMessageBundleId: String?
}
