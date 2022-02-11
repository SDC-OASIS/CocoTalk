//
//  ModelChatMessage.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation

struct ModelChatMessage: Codable {
    var id: String?
    var roomId: String?
    var messageBundleId: String?
    var userId: Int?
    var content: String?
    var type: Int?
    var sentAt: String?
}
