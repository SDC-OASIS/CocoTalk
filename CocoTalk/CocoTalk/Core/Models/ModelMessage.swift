//
//  ModelMessage.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/21.
//

import Foundation

struct ModelMessage {
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

extension ModelMessage {
    #warning("랜덤 메시지 함수 삭제")
    static func getRandomMessage(id: Int) -> ModelMessage {
        let text = Bool.random() ? "첫 번째 줄" : "^.^ 오늘 재택하신 분들은 내일부터 슥슥 가져가심됩니다. 참고로 섭취 가능 기한은 1주일!"
        return ModelMessage(id: id, text: text, mediaType: 0, mediaUrls: nil, senderId: Int.random(in: 0..<4), date: Date.now, isMe: Bool.random(), hasTail: Bool.random())
    }
}
