//
//  AuthRepository.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/07.
//

import Foundation
import RxSwift
import Moya
import RxMoya
import SwiftKeychainWrapper


/// 인증 관련 네트워킹을 지원하는 레포지토리
class AuthRepository {
    private let provider: MoyaProvider<AuthAPI>
    
    init() {
        provider = MoyaProvider<AuthAPI>()
    }

    func reissueToken(_ token: String) -> Observable<APIResult_0<ModelSigninResponse>> {
        return provider.rx.request(.reissueToken(token))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_0<ModelSigninResponse>.self, from: $0.data) }
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }
    
    func verifyToken(_ token: String) -> Observable<APIResult_0<ModelEmailVerifyResponse>> {
        return provider.rx.request(.verifyToken(token))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_0<ModelEmailVerifyResponse>.self, from: $0.data) }
            .catchAndReturn(APIResult_0<ModelEmailVerifyResponse>())
    }
    

    func signin(cid: String, password: String, fcmToken: String) -> Observable<APIResult_0<ModelSigninResponse>> {
        let data = ModelSigninRequest(cid: cid, password: password, fcmToken: fcmToken)
        return provider.rx.request(.signin(data))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_0<ModelSigninResponse>.self, from: $0.data) }
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }
    
    func requestEmailAuthenticationCode(with email: String) -> Observable<APIResult_0<ModelIssueEmailCodeResponse>> {
        return provider.rx.request(.issueEmailCode(ModelIssueEmailCodeRequest(email: email)))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_0<ModelIssueEmailCodeResponse>.self, from: $0.data) }
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }

    func verifyEmail(email: String, code: String) -> Observable<APIResult_0<ModelEmailVerifyResponse>> {
        let data = ModelEmailVerifyRequest(email: email, code: code)
        return provider.rx.request(.verifyEmail(data))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_0<ModelEmailVerifyResponse>.self, from: $0.data) }
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }

    func signup(with signupData: ModelSignupData) -> Observable<APIResult_0<ModelSignupResponse>> {
        let data = ModelSignupRequest(cid: signupData.cid,
                                      password: signupData.password,
                                      username: signupData.userName,
                                      nickname: signupData.nickname,
                                      phone: signupData.phone,
                                      email: signupData.email,
                                      status: signupData.status,
                                      profile: signupData.profileImageUrl,
                                      profileThumbnail: signupData.profileThumbnailUrl)
        return provider.rx.request(.signup(data))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_0<ModelSignupResponse>.self, from: $0.data) }
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }

    func isIdExist(id: String) -> Observable<APIResult_1<ModelProfile>> {
        return provider.rx.request(.isIdExist(id))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_1<ModelProfile>.self, from: $0.data)}
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }
    
    func isEmailExist(email: String) -> Observable<APIResult_1<ModelProfile>> {
        return provider.rx.request(.isEmailExist(email))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_1<ModelProfile>.self, from: $0.data)}
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }
    
    func isPhoneExist(phone: String) -> Observable<APIResult_1<[ModelProfile]>> {
        return provider.rx.request(.isPhoneExist(phone))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_1<[ModelProfile]>.self, from: $0.data)}
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }
}

