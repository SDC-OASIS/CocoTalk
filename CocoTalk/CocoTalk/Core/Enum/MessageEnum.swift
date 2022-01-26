//
//  Message.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/21.
//

import Foundation

enum MessageType: Int, Codable {
    case text
    case photo
    case video
    case emoji
    case file
}

enum MessagePosition {
    case send
    case receive
    case unknown
}


