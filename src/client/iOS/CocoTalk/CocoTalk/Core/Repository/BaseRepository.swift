//
//  BaseRepository.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/02.
//

import Foundation
import RxSwift

protocol BaseRepository {
    associatedtype ItemType: Decodable
    
    var items: [ItemType] { get }
    
    func initFetch() -> [ItemType]
    
    func fetchFromServer(with token: String) -> Observable<[ItemType]?>
    
    func insert(_ newItem: ItemType) -> Bool

    func update(item: ItemType) -> Bool
    
    func delete() -> Bool
    
    func search(query: String) -> [ItemType]
}
