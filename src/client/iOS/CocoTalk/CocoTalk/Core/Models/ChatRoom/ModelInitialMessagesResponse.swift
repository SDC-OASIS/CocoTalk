//
//  ModelInitialMessagesResponse.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/15.
//

import Foundation

/// 채팅방 입장시 메시지를 불러오는 모델
struct ModelInitialMessagesResponse: Codable {
    var room: ModelRoom?
    var messageList: [ModelMessage]?
}
