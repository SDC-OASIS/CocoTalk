//
//  ChatRoomRepository.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/02.
//

import Foundation
import RxSwift
import RxRelay
import CoreData
import Moya
import RxMoya

class ChatRoomRepository {
    typealias ItemType = ModelRoomList
    
    private let provider: MoyaProvider<RoomAPI>
    
    init() {
        provider = MoyaProvider<RoomAPI>()
    }
    
    static var items: [ItemType] = []
    
    static var chatRooms: [ItemType] {
        get {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"
            
            return items.sorted(by: {
                let aDate = dateFormatter.date(from: $0.recentChatMessage?.sentAt ?? "") ?? Date()
                let bDate = dateFormatter.date(from: $1.recentChatMessage?.sentAt ?? "") ?? Date()
                return aDate > bDate
            })
        }
    }
    
    func initFetch() -> [ItemType] {
        return Self.items
    }
    
    func fetchFromServer(with token: String) -> Observable<APIResult_1<[ItemType]>> {
        return provider.rx.request(.fetchRooms(token))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_1<[ItemType]>.self, from: $0.data) }
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }
    
    func createChatRoom(with token: String, data: ModelCreateChatRoomRequest) -> Observable<APIResult_1<ModelRoom>>{
        return provider.rx.request(.createRoom(token, data: data))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_1<ModelRoom>.self, from: $0.data) }
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }
    
    func checkRoomExist(with token: String, memberId: String) -> Observable<APIResult_1<ModelRoom>> {
        return provider.rx.request(.checkRoomExist(token, memberId: memberId))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_1<ModelRoom>.self, from: $0.data) }
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }
    
    func fetchRoomInfo(with token: String, roomId: String) -> Observable<APIResult_1<ModelRoom>> {
        return provider.rx.request(.fetchRoomInfo(token, roomId: roomId))
            .retry(3)
            .asObservable()
            .map { try JSONDecoder().decode(APIResult_1<ModelRoom>.self, from: $0.data) }
            .catch { error in
                print(error)
                return Observable.error(error)
            }
    }
    
    func insert(_ newItem: ItemType) -> Bool {
        return true
    }
    
    func update(item: ItemType) -> Bool {
        return true
    }
    
    func delete() -> Bool {
        return true
    }
    
    func search(query: String) -> [ItemType] {
        return Self.items
    }
}
