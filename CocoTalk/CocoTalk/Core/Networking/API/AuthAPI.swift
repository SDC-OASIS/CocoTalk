//
//  AuthAPI.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/07.
//

import Foundation
import Moya

enum AuthAPI {
    case verifyToken(_ token: String)
    case postFCMToken(_ data: ModelPostFCMTokenRequest)
    case signin(_ data: ModelSigninRequest)
    case signup(_ data: ModelSignupRequest)
    case issueEmailCode(_ data: ModelIssueEmailCodeRequest)
    case verifyEmail(_ data: ModelEmailVerifyRequest)
    case isIdExist(_ id: String)
    case isEmailExist(_ email: String)
    case isPhoneExist(_ phone: String)
}

extension AuthAPI: TargetType {
    var baseURL: URL {
        return .baseURL
    }
    
    var path: String {
        switch self {
        case .verifyToken(_):
            return "/auth/device"
        case .postFCMToken(_):
            return "/push/device"
        case .signin(_):
            return "/auth/signin"
        case .signup(_):
            return "/auth/signup"
        case .issueEmailCode(_):
            return "/auth/email/issue"
        case .verifyEmail(_):
            return "/auth/email/validation"
        case .isIdExist(let id):
            return "/user/cid/\(id)"
        case .isEmailExist(let email):
            return "/user/email/\(email)"
        case .isPhoneExist(_):
            return "/user/phone"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .postFCMToken(_), .signin(_), .signup(_), .issueEmailCode(_),.verifyEmail(_):
            return .post
        case .verifyToken(_), .isIdExist(_), .isEmailExist(_), .isPhoneExist(_):
            return .get
        }
    }
    
    var task: Task {
        switch self {
        case .verifyToken(_):
            return .requestPlain
        case .postFCMToken(let data):
            return .requestJSONEncodable(data)
        case .signin(let data):
            return .requestJSONEncodable(data)
        case .signup(let data):
            return .requestJSONEncodable(data)
        case .issueEmailCode(let data):
            return .requestJSONEncodable(data)
        case .verifyEmail(let data):
            return .requestJSONEncodable(data)
        case .isIdExist(_):
            return .requestPlain
        case .isEmailExist(_):
            return .requestPlain
        case .isPhoneExist(let phone):
            return .requestParameters(parameters: ["phones": phone], encoding: URLEncoding.queryString)
        }
    }
    
    var headers: [String : String]? {
        var parameters: [String : String] = ["Content-type": "application/json"]
        
        switch self {
        case .verifyToken(let token):
            parameters["X-ACCESS-TOKEN"] = token
        default: break
        }
        
        return parameters
    }
}
