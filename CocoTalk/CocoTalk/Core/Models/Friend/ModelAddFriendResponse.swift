//
//  ModelAddFriendResponse.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/12.
//

import Foundation

struct ModelAddFirendResponse: Codable {
    var id: Int?
    var fromUser: ModelProfile?
    var toUser: ModelProfile?
    var hidden: Bool?
}
