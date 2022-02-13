//
//  RoomAPI.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation
import Moya

enum RoomAPI {
    case fetchRoomInfo(_ token: String, roomId: String)
    case createRoom(_ token: String, data: ModelCreateChatRoomRequest)
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
        case .createRoom(_, _):
            return "/chat/rooms"
        case .checkRoomExist(_, memberId: let memberId):
            return "/chat/rooms/private/\(memberId)"
        case .fetchRoomInfo(_, let roomId):
            return "/chat/rooms/\(roomId)"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .fetchRooms(_):
            return .get
        case .createRoom(_, _):
            return .post
        case .checkRoomExist(_,_):
            return .get
        case .fetchRoomInfo(_,_):
            return .get
        }
    }
    
    var task: Task {
        switch self {
        case .fetchRooms(_):
            return .requestPlain
        case .createRoom(_, let data):
            return .requestJSONEncodable(data)
        case .checkRoomExist(_,_):
            return .requestPlain
        case .fetchRoomInfo(_,_):
            return .requestPlain
        }
    }
    
    var headers: [String : String]? {
        var parameters: [String : String] = ["Content-type": "application/json"]
        
        switch self {
        case .fetchRooms(let token):
            parameters["X-ACCESS-TOKEN"] = token
        case .createRoom(let token, _):
            parameters["X-ACCESS-TOKEN"] = token
        case .checkRoomExist(let token, _):
            parameters["X-ACCESS-TOKEN"] = token
        case .fetchRoomInfo(let token,_):
            parameters["X-ACCESS-TOKEN"] = token
        }
        
        return parameters
    }
    
    
}
