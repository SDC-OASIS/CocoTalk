//
//  ModelMessage.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/21.
//

import Foundation

struct ModelMessage: Codable {
    /// 메시지 id
    var id: Int?
    
    /// 메시지 텍스트
    var text: String?
    
    /// 사진/동영상/이모지
    var mediaType: Int?
    
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
}
