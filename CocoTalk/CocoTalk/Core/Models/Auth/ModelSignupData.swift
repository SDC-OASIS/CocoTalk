//
//  ModelSignupData.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/04.
//

import Foundation

struct ModelSignupData: Codable {
    var userName: String = ""
    var profileImageUrl: String = ""
    var email: String = ""
    var phone: String = ""
    var cid: String = ""
    var birth: String = "" // not required
    var nickname: String = ""
    var terms: Bool = false
    var password: String = ""
    var provider: String = "local"
    var providerId: String = ""
    var profile: String = "" // not required
    var status: Int = 0
}
