//
//  AuthAPI.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/07.
//

import Foundation
import Moya
import UIKit

/// 인증 API
enum AuthAPI {
    case reissueToken(_ token: String)
    case verifyToken(_ token: String)
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
        case .reissueToken(_):
            return "/auth/reissue"
        case .verifyToken(_):
            return "/auth/device"
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
        case .signin(_), .signup(_), .issueEmailCode(_),.verifyEmail(_):
            return .post
        case .reissueToken(_), .verifyToken(_), .isIdExist(_), .isEmailExist(_), .isPhoneExist(_):
            return .get
        }
    }
    
    var task: Task {
        switch self {
        case .reissueToken(_):
            return .requestPlain
        case .verifyToken(_):
            return .requestPlain
        case .signin(let data):
            return .requestJSONEncodable(data)
        case .signup(let data):
            let imageData = UIImage.load(fileName: data.profile)?.jpegData(compressionQuality: 1.0)
            let imageThumbnailData = UIImage.load(fileName: data.profileThumbnail)?.jpegData(compressionQuality: 1.0)
            let cidData = data.cid.data(using: String.Encoding.utf8) ?? Data()
            let passwordData = data.password.data(using: String.Encoding.utf8) ?? Data()
            let usernameData = data.username.data(using: String.Encoding.utf8) ?? Data()
            let nicknameData = data.nickname.data(using: String.Encoding.utf8) ?? Data()
            let phoneData = data.phone.data(using: String.Encoding.utf8) ?? Data()
            let emailData = data.email.data(using: String.Encoding.utf8) ?? Data()
            let statusData = data.status.description.data(using: String.Encoding.utf8) ?? Data()
            
            var formData: [Moya.MultipartFormData] = []
            formData.append(Moya.MultipartFormData(provider: .data(imageData!), name: "profileImg", fileName: "profile.jpeg", mimeType: "image/jpeg"))
            formData.append(Moya.MultipartFormData(provider: .data(imageThumbnailData!), name: "profileImgThumb", fileName: "profile_thumbnail.jpeg", mimeType: "image/jpeg"))
            formData.append(Moya.MultipartFormData(provider: .data(cidData), name: "cid"))
            formData.append(Moya.MultipartFormData(provider: .data(passwordData), name: "password"))
            formData.append(Moya.MultipartFormData(provider: .data(usernameData), name: "username"))
            formData.append(Moya.MultipartFormData(provider: .data(nicknameData), name: "nickname"))
            formData.append(Moya.MultipartFormData(provider: .data(phoneData), name: "phone"))
            formData.append(Moya.MultipartFormData(provider: .data(emailData), name: "email"))
            formData.append(Moya.MultipartFormData(provider: .data(statusData), name: "status"))
            return .uploadMultipart(formData)
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
        case .signup(_):
            parameters["Content-type"] = "multipart/form-data"
            break
        case .reissueToken(let token):
            parameters["X-REFRESH-TOKEN"] = token
        case .verifyToken(let token):
            parameters["X-ACCESS-TOKEN"] = token
        default: break
        }
        
        return parameters
    }
}
