//
//  ModelUserResponse.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/07.
//

import Foundation

struct ModelUserResponse: Decodable {
    var id: Int?
    var cid: String?
    var userName: String?
    var nickname: String?
    var birth: String?
    var phone: String?
    var email: String?
    var status: Int?
    var loggedinAt: String?
    var profile: String?
}
