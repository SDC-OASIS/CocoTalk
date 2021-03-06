//
//  APIResult_1.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/07.
//

import Foundation

/// 종훈님 API
struct APIResult_1<T: Decodable>: Decodable {
    var status: String?
    var statusCode: Int?
    var message: String?
    var data: T?
    var timestamp: String?
    var error: API_1_Error?
}

struct API_1_Error: Decodable {
    var code: Int?
    var desc: String?
    var type: String?
}
