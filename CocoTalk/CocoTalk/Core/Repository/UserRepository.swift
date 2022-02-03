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

class UserRepository: BaseRepository {
    typealias ItemType = ModelProfile
    
    var items: [ItemType] = []
    
    func initFetch() -> [ItemType] {
        return items
    }
    
    func fetchFromServer() -> Observable<[ItemType]> {
        return Observable.create { _ in
            return Disposables.create()
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

