//
//  Message.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/21.
//

import Foundation

enum MessageTypeEnum: Int, Codable {
    case text
    case photo
    case video
    case emoji
    case file
}

enum MessagePositionEnum {
    case send
    case receive
    case unknown
}


