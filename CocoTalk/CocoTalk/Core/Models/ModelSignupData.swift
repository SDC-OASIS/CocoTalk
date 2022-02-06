//
//  ModelSignupData.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/04.
//

import Foundation

struct ModelSignupData: Codable {
    var name: String = ""
    var profileImageUrl: String = ""
    var email: String = ""
    var phoneNumber: String = ""
    var cocotalkId: String = ""
    var birth: String = ""
    var nickname: String = ""
    var terms: Bool = false
    var password: String = ""
}

extension ModelSignupData {    
    
    func encode() -> Data? {
        if let encoded = try? JSONEncoder().encode(self) {
            return encoded
        }
        return nil
    }
    
    static func decode(savedData: Data) -> ModelSignupData? {
        if let decoded = try? JSONDecoder().decode(ModelSignupData.self, from: savedData) {
            return decoded
        }
        return nil
    }
}
