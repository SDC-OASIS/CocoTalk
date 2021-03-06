//
//  MessageRepository.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/02.
//

import Foundation
import RxSwift
import RxRelay
import CoreData

class MessageRepository {
    typealias ItemType = ModelMessage
    
    var items: [ItemType] = []
    
    func initFetch() -> [ItemType] {
        return items
    }
    
    func fetchFromServer(with token: String) -> Observable<[ItemType]?> {
        return Observable.create { _ in
            return Disposables.create()
        }
    }
    
    func insert(_ newItem: ItemType) -> Bool {
        items.append(newItem)
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


