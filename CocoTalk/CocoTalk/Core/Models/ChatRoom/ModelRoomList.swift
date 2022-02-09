//
//  ModelRoomList.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation

struct ModelRoomList: Codable {
    var room: ModelRoom?
    var recentChatMessage: ModelChatMessage?
    var recentMessageBundleCount: Int?
    var unreadNumber: Int?
}
