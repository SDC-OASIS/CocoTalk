//
//  UserAPI.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation
import Moya

enum UserAPI {
    case addFriend(_ cid: Int, _ token: String)
    case findUserByCid(_ cid: String, _ token: String)
    case fetchMyProfile(_ token: String)
    case fetchFriends(_ token: String)
}

extension UserAPI: TargetType {
    var baseURL: URL {
        return .baseURL
    }
    
    var path: String {
        switch self {
        case .addFriend(_,_):
            return "/user/friends"
        case .findUserByCid(let cid, _):
            return "/user/cid/\(cid)"
        case .fetchMyProfile(_):
            return "/user/token"
        case .fetchFriends(_):
            return "/user/friends"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .addFriend(_,_):
            return .post
        case .findUserByCid(_,_):
            return .get
        case .fetchMyProfile(_):
            return .get
        case .fetchFriends(_):
            return .get
        }
    }
    
    var task: Task {
        switch self {
        case .addFriend(let id,_):
            return .requestJSONEncodable(ModelAddFirendRequest(toUid: id))
        case .findUserByCid(_, _):
            return .requestPlain
        case .fetchMyProfile(_):
            return .requestPlain
        case .fetchFriends(_):
            return .requestPlain
        }
    }
    
    var headers: [String : String]? {
        var parameters: [String : String] = ["Content-type": "application/json"]
        
        switch self {
        case .addFriend(_, let token),
                .findUserByCid(_, let token),
                .fetchMyProfile(let token),
                .fetchFriends(let token):
            parameters["X-ACCESS-TOKEN"] = token
        }
        
        return parameters
    }
    
}
