//
//  ModelSignupRequest.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/07.
//

import Foundation

#warning("회원가입 리퀘스트 데이터 수정 필요함")
struct ModelSignupRequest: Codable {
    var cid: String = ""
    var password: String = ""
    var username: String = ""
    var nickname: String = ""
    var phone: String = ""
    var email: String = ""
    var status: Int = 0
    var profile: String = "" // not required
}
