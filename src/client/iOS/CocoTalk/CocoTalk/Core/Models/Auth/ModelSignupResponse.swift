//
//  ModelSignupResponse.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/07.
//

import Foundation

struct ModelSignupResponse: Codable {
    var id: Int?
    var cid: String?
    var username: String?
    var nickname: String?
    var email: String?
    var phone: String?
    var profile: String?
    var birth: String?
}
