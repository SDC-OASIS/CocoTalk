//
//  APIResult_Device.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/08.
//

import Foundation

struct APIResult_Device: Codable {
    var id: Int?
    var token: String?
    var userId: Int?
    var ip: String?
    var agent: String?
    var type: Int?
    var loggedinAt: String?
}
