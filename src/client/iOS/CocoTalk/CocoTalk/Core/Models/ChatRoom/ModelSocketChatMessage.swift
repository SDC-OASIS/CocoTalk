//
//  ModelChatMessage.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation


struct ModelMessageVO: Codable {
    /// 메시지 정보
    var message: ModelSubChatMessage?
    
    /// 번들 정보
    var bundleInfo: ModelMessageBundle?
}

/// 소켓으로 수신하는 메시지
struct ModelSubChatMessage: Codable {
    /// 메시지 id
    var id: String?
    
    /// 룸 id
    var roomId: String?
    
    /// 메시지 번들 id
    var messageBundleId: String?
    
    /// 보낸 유저 id
    var userId: Int?
    
    /// 보낸 컨텐츠
    var content: String?
    
    /// 보낸 메시지 타입
    /// 0: 일반 메시지
    /// 1: 초대
    /// 2: 나가기
    /// 3: join
    /// 4: 사진
    /// 5: 영상
    /// 6: 파일
    var type: Int?
    
    /// 보낸 시간
    var sentAt: String?
}

///// 소켓으로 수신하는 메시지
//struct ModelSubChatMessage: Codable {
//    /// 채팅방 id
//    var id: String?
//
//    /// 채팅방 이름
//    var roomname: String?
//
//    /// 메시지 번들 id 목록
//    var messageBundleIds: String?
//
//    /// 보낸 유저 id
//    var userId: Int?
//
//    /// 보낸 컨텐츠
//    var content: String?
//
//    /// 보낸 메시지 타입
//    var type: Int?
//
//    /// 보낸 시간
//    var sentAt: String?
//}

/// 소켓으로 보내는 초대 메시지
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

/// 소켓으로 보내는 채팅 메시지
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
