//
//  ModelMessage.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/21.
//

import Foundation

struct ModelMessage: Codable {    
    /// 메시지 id
    var id: String?
    
    /// 채팅방 id
    var roomId: String?
    
    /// 메시지 번들 id
    var messageBundleId: String?
    
    /// user id
    var userId: Int?
    
    /// 메시지 텍스트
    var content: String?
    
    /// 메시지 타입
    /// - 0: 단순 메시지
    /// - 1: 입장 메시지 (XX 님이 입장하셨습니다.)
    /// - 2: 퇴장 메시지 (XX 님이 퇴장하셨습니다)
    /// - 3: 초대 메시지 (XX 님이 초대됐습니다.)
    /// - 4: 사진
    /// - 5: 동영상
    /// - 6: 그 외 파일
    var type: Int?
    
    /// 보낸 시각
    var sentAt: String?
    
    /// 미디어 리소스 주소
    var mediaUrls: [String]?
    
    /// 송신자
    ///
    /// nil: 안내 메시지
    var senderId: Int?
    
    /// 송신 날짜
    var date: Date?
    
    var isMe: Bool?
    
    var hasTail: Bool?
    
    var hasDate: Bool?
    
    var username: String?
    
    var profileImageURL: String?
}
