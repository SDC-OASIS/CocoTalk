//
//  ModelInitialMessagesResponse.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/15.
//

import Foundation

struct ModelInitialMessagesResponse: Codable {
    var room: ModelRoom?
    var messageList: [ModelMessage]?
}
