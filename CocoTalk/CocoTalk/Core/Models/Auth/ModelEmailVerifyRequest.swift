//
//  ModelEmailVerifyRequest.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/07.
//

import Foundation

struct ModelEmailVerifyRequest: Codable {
    var email: String
    var code: String
}
