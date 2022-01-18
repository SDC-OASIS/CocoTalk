//
//  ModelChatRoom.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/18.
//

import Foundation

struct ModelChatRoom: Codable {
    var id: Int?
    var roomImageURL: String?
    var roomTitle: String?
    var roomMemberNumber: Int?
    var roomMemebrs: [ModelProfile]?
    var unreadMessageNumber: Int?
    var lastMessage: String?
    var lastMessageTime: Date?
    var order: Int?
    var isPinned: Bool?
    var isFavorite: Bool?
    var isSilent: Bool?
    #warning("확정되면 ENUM으로 생성")
    var roomType: Int?
}

extension ModelChatRoom {
    static func createRandom() -> ModelChatRoom {
        let title = Bool.random() ? UUID().uuidString.split(separator: "-")[0].description : UUID().uuidString
        let lastMessage = Bool.random() ? "짧은 메시지" : "이 헌법공포 당시의 국회의원의 임기는 제1항에 의한 국회의 최초의 집회일 전일까지로 한다. 체포·구속·압수 또는 수색을 할 때에는 적법한 절차에 따라 검사의 신청에 의하여 법관이 발부한 영장을 제시하여야 한다. 다만, 현행범인인 경우와 장기 3년 이상의 형에 해당하는 죄를 범하고 도피 또는 증거인멸의 염려가 있을 때에는 사후에 영장을 청구할 수 있다."
        let memberNum = Int.random(in: 2..<5)
        var members: [ModelProfile] = []
        for _ in 2..<memberNum {
            members.append(ModelProfile.createRandomProfile())
        }

        return ModelChatRoom.init(id: Int.random(in: 0..<99999), roomTitle: title, roomMemberNumber: memberNum, roomMemebrs: members, unreadMessageNumber: Int.random(in: 0..<100), lastMessage: lastMessage, lastMessageTime: Date(timeIntervalSince1970: 1642487018), order: 0, isPinned: Bool.random(), isFavorite: Bool.random(), isSilent: Bool.random(), roomType: 0)
    }
}
