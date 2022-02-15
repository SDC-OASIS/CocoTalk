//
//  ModelChatMessage.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation

struct ModelSubChatMessage: Codable {
    var id: String?
    var roomId: String?
    var messageBundleId: String?
    var userId: Int?
    var content: String?
    var type: Int?
    var sentAt: String?
}

struct ModelPubInvite: Codable {
    var roomId: String?
    /// 룸 타입
    /// 0: 개인톡,
    /// 1: 단체톡
    var roomType: Int?
    var roomname: String?
    var userId: Int?
    var username: String?
    var messageBundleId: String?
    var receiverIds: [String]?
    /// 메시지 타입
    /// - 0: 단순 메시지
    /// - 1: 입장 메시지 (XX 님이 입장하셨습니다.)
    /// - 2: 퇴장 메시지 (XX 님이 퇴장하셨습니다)
    /// - 3: 초대 메시지 (XX 님이 초대됐습니다.)
    /// - 4: 사진
    /// - 5: 동영상
    /// - 6: 그 외 파일
    var type: Int?
    var content: String?
    var invitees: [UserWithPlainStringProfile]?
}

struct ModelPubChatMessage: Codable {
    var roomId: String?
    /// 룸 타입
    /// 0: 개인톡,
    /// 1: 단체톡
    var roomType: Int?
    var roomname: String?
    var userId: Int?
    var username: String?
    var messageBundleId: String?
    var receiverIds: [String]?
    /// 메시지 타입
    /// - 0: 단순 메시지
    /// - 1: 입장 메시지 (XX 님이 입장하셨습니다.)
    /// - 2: 퇴장 메시지 (XX 님이 퇴장하셨습니다)
    /// - 3: 초대 메시지 (XX 님이 초대됐습니다.)
    var type: Int?
    var content: String?
}

struct ModelMessageBundle: Codable {
    var currentMessageBundleCount: Int?
    var currentMessageBundleId: String?
    var nextMessageBundleId: String?
}
