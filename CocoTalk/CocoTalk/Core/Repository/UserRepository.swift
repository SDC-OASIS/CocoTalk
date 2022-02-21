//
//  UserRepository.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/03.
//

import Foundation
import RxSwift
import RxRelay
import CoreData
import Moya
import RxMoya
import SwiftKeychainWrapper


/// 유저 정보에 대한 네트워킹을 지원하는 레포지토리
class UserRepository {
    /// ModelFriend
    typealias ItemType = ModelProfile
    
    private let provider: MoyaProvider<UserAPI>
    
    init() {
        provider = MoyaProvider<UserAPI>()
    }
    
    static var items: [ItemType] = []
    
    static var myProfile: ModelProfile? 
    
#warning("코어 데이터에서 불러오기")
    func initFetch() -> [ItemType] {
        return UserRepository.items
    }
    
    func fetchMyProfile() -> ModelProfile {
        return ModelProfile(id: -1, username: "병학", bio: "안녕하세요 ✌️")
    }
    
    func fetchMyProfileFromServer(with token: String) ->  Observable<ModelProfile?> {
        return provider.rx.request(.fetchMyProfile(token))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_1<ModelProfile>.self, from: $0.data) }
            .map { $0.data }
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }
    
    func fetchFromServer(with token: String) -> Observable<[ModelFriend]?> {
        return provider.rx.request(.fetchFriends(token))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_1<[ModelFriend]>.self, from: $0.data) }
            .map { $0.data }
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }
    
    /// 코코톡 아이디로 유저 찾기
    func findUserByCid(_ cid: String, token: String) -> Observable<ModelProfile?> {
        return provider.rx.request(.findUserByCid(cid, token))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_1<ModelProfile>.self, from: $0.data) }
            .map { $0.data }
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }
    
    func addFriend(_ id: Int, token: String) -> Observable<ModelAddFirendResponse?> {
        return provider.rx.request(.addFriend(id, token))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_1<ModelAddFirendResponse>.self, from: $0.data) }
            .map { $0.data }
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }
    
    func update(item: ItemType) -> Bool {
        return true
    }
    
    func delete() -> Bool {
        return true
    }
    
    /// Search
    /// 유저네임으로 찾기
    func search(query: String) -> [ItemType] {
        if query.isEmpty {
            return UserRepository.items
        }
        return UserRepository.items.filter { ($0.username ?? "").contains(query) }
    }
    
    func findUserById(_ userId: Int) -> ItemType? {
        return UserRepository.items.filter { $0.id ?? -1 == userId }.first
    }
}
