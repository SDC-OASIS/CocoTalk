//
//  ModelUser.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/03.
//

import Foundation

struct ModelUser: Codable {
    var id: Int?
    var name: String?
    var bio: String?
    var bgImageUrl: String?
    var profileImageUrl: String?
    var email: String?
    var phoneNumber: String?
    var cocotalkId: String?
}
