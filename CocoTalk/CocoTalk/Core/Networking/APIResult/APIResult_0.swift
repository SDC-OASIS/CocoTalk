//
//  APIResult_0.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/07.
//

import Foundation

/// 민정님 API
struct APIResult_0<T: Decodable>: Decodable {
    var isSuccess: Bool?
    var status: Int?
    var code: Int?
    var message: String?
    var result: T?
    var timestamp: String?
}
