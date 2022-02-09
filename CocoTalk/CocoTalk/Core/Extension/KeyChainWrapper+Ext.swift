//
//  KeyChainWrapper+Ext.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/07.
//

import Foundation
import SwiftKeychainWrapper

extension KeychainWrapper.Key {
    
    static let accessToken: KeychainWrapper.Key = "accessToken"
    static let refreshToken: KeychainWrapper.Key = "refreshToken"
    static let fcmToken: KeychainWrapper.Key = "fcmToken"
    
}
