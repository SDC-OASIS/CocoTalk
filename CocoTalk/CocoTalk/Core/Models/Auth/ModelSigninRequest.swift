//
//  ModelSigninRequest.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/07.
//

import Foundation

struct ModelSigninRequest: Codable {
    var cid: String
    var password: String
    var fcmToken: String
}
