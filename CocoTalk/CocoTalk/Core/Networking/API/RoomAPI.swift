//
//  RoomAPI.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation
import Moya

enum RoomAPI {
    case fetchInitialMessages(_ token: String, roomId: String, count: Int)
    case fetchRoomInfo(_ token: String, roomId: String)
    case fetchRooms(_ token: String)
    case checkRoomExist(_ token: String, memberId: String)
}

extension RoomAPI: TargetType {
    var baseURL: URL {
        .baseURL
    }
    
    var path: String {
        switch self {
        case .fetchRooms(_):
            return "/chat/rooms/list"
        case .checkRoomExist(_, memberId: let memberId):
            return "/chat/rooms/private/\(memberId)"
        case .fetchRoomInfo(_, let roomId):
            return "/chat/rooms/\(roomId)"
        case .fetchInitialMessages(_, let roomId,_):
            return "/chat/rooms/\(roomId)/tail/"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .fetchRooms(_):
            return .get
        case .checkRoomExist(_,_):
            return .get
        case .fetchRoomInfo(_,_):
            return .get
        case .fetchInitialMessages(_,_,_):
            return .get
        }
    }
    
    var task: Task {
        switch self {
        case .fetchRooms(_):
            return .requestPlain
        case .checkRoomExist(_,_):
            return .requestPlain
        case .fetchRoomInfo(_,_):
            return .requestPlain
        case .fetchInitialMessages(_,_,let count):
            return .requestParameters(parameters: ["count": count], encoding: URLEncoding.queryString)
        }
    }
    
    var headers: [String : String]? {
        var parameters: [String : String] = ["Content-type": "application/json"]
        
        switch self {
        case .fetchRooms(let token):
            parameters["X-ACCESS-TOKEN"] = token
        case .checkRoomExist(let token, _):
            parameters["X-ACCESS-TOKEN"] = token
        case .fetchRoomInfo(let token,_):
            parameters["X-ACCESS-TOKEN"] = token
        case .fetchInitialMessages(let token,_,_):
            parameters["X-ACCESS-TOKEN"] = token
        }
        
        return parameters
    }
    
    
}
