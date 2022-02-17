//
//  ModelMessage.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/21.
//

import Foundation

struct ModelMessage: Codable {    
    /// 메시지 id
    var id: String? = nil
    
    /// 채팅방 id
    var roomId: String? = nil
    
    /// 메시지 번들 id
    var messageBundleId: String? = nil
    
    /// user id
    var userId: Int? = nil
    
    /// 메시지 텍스트
    var content: String? = nil
    
    /// 메시지 타입
    /// - 0: 단순 메시지
    /// - 1: 입장 메시지 (XX 님이 입장하셨습니다.)
    /// - 2: 퇴장 메시지 (XX 님이 퇴장하셨습니다)
    /// - 3: 초대 메시지 (XX 님이 초대됐습니다.)
    /// - 4: 사진
    /// - 5: 동영상
    /// - 6: 그 외 파일
    var type: Int? = nil
    
    /// 보낸 시각
    var sentAt: String? = nil
    
    /// 미디어 리소스 주소
    var mediaUrls: [String]? = nil
    
    /// 송신자
    ///
    /// nil: 안내 메시지
    var senderId: Int? = nil
    
    /// 송신 날짜
    var date: Date? = nil
    
    /// 내가 보낸 톡인지
    var isMe: Bool? = nil
    
    /// 꼬리 붙임 여부
    var hasTail: Bool? = nil
    
    /// 날짜 표시 여부
    var hasDate: Bool? = nil
    
    /// 유저 네임
    var username: String? = nil
    
    /// 프로필 이미지 URL
    var profileImageURL: String? = nil
    
    /// 안 읽은 사람 수
    var unreadMemberCount: Int? = nil
}
