//
//  ModelSignupRequest.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/07.
//

import Foundation

struct ModelSignupRequest: Codable {
    var cid: String = ""
    var password: String = ""
    var username: String = ""
    var nickname: String = ""
//    var birth: String = "" // not required
    var phone: String = ""
    var email: String = ""
    var status: Int = 0
    var profile: String = "" // not required
}
