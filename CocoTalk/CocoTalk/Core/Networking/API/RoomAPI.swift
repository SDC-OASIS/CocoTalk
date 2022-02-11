//
//  RoomAPI.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation
import Moya

enum RoomAPI {
    case fetchRooms(_ token: String)
}

extension RoomAPI: TargetType {
    var baseURL: URL {
        .baseURL
    }
    
    var path: String {
        switch self {
        case .fetchRooms(_):
            return "/chat/rooms/list"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .fetchRooms(_):
            return .get
        }
    }
    
    var task: Task {
        switch self {
        case .fetchRooms(_):
            return .requestPlain
        }
    }
    
    var headers: [String : String]? {
        var parameters: [String : String] = ["Content-type": "application/json"]
        
        switch self {
        case .fetchRooms(let token):
            parameters["X-ACCESS-TOKEN"] = token
        }
        
        return parameters
    }
    
    
}
