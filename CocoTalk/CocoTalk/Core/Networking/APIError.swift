//
//  APIError.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/02.
//

import Foundation

enum APIError: String, Error {
    case serverError
    case noData
    case dataDecodingError
}
