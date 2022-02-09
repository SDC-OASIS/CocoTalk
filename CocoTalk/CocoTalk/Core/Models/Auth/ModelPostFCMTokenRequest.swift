//
//  ModelPostFCMTokenRequest.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/08.
//

import Foundation

struct ModelPostFCMTokenRequest: Codable {
    var userId: Int
    var fcmToken: String
}
