//
//  Encodable+Ext.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation

extension Encodable {
    func encode() -> Data? {
        if let encoded = try? JSONEncoder().encode(self) {
            return encoded
        }
        return nil
    }
    
    var dictionary: [String: Any]? {
        guard let data = try? JSONEncoder().encode(self) else { return nil }
        return (try? JSONSerialization.jsonObject(with: data, options: .allowFragments)).flatMap { $0 as? [String: Any] }
    }
}
