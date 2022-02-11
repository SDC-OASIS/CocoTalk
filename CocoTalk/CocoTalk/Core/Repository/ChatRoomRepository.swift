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

class ChatRoomRepository: BaseRepository {
    typealias ItemType = ModelRoomList
    
    private let provider: MoyaProvider<RoomAPI>
    
    init() {
        provider = MoyaProvider<RoomAPI>()
    }
    
    var items: [ItemType] = []
    
    func initFetch() -> [ItemType] {
        return items
    }
    
    func fetchFromServer(with token: String) -> Observable<[ItemType]?> {
        return provider.rx.request(.fetchRooms(token))
            .retry(3)
            .asObservable()
            .map { try? JSONDecoder().decode(APIResult_1<[ModelRoomList]>.self, from: $0.data) }
            .map { $0?.data }
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
        return items
    }
}


/*
 {
             "room": {
                 "id": "6204e68642a9385c78f2ead4",
                 "roomname": "단체 채팅방",
                 "img": "채팅방 이미지",
                 "type": 1,
                 "members": [
                     {
                         "userId": 64,
                         "username": "고병학",
                         "profile": "프로필",
                         "joining": true,
                         "joinedAt": "2022-02-10T10:18:46.792",
                         "enteredAt": "2022-02-10T10:18:46.792",
                         "awayAt": "2022-02-10T10:18:46.792",
                         "leftAt": "2022-02-10T10:18:46.792"
                     },
                     {
                         "userId": 93,
                         "username": "권희은",
                         "profile": "프로필",
                         "joining": true,
                         "joinedAt": "2022-02-10T10:18:46.792",
                         "enteredAt": "2022-02-10T10:18:46.792",
                         "awayAt": "2022-02-10T10:18:46.792",
                         "leftAt": "2022-02-10T10:18:46.792"
                     }
                 ],
                 "messageBundleIds": "[6204e68642a9385c78f2ead5]",
                 "noticeIds": "[]"
             },
             "recentChatMessage": {
             },
             "recentMessageBundleCount": 0,
             "unreadNumber": 0
         }
 
 */
