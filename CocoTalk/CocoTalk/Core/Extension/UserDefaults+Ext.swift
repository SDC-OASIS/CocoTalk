//
//  UserDefaults+Ext.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/06.
//

import Foundation

extension UserDefaults {
    
    static func resetSignupData() {
        // 회원가입 할 때 생기는 임시 프로필 이미지 삭제
        if let savedData = UserDefaults.standard.object(forKey: UserDefaultsKey.signupData.rawValue) as? Data,
           let signupData = ModelSignupData.decode(savedData: savedData) {
            if !signupData.profileImageUrl.isEmpty {
                let fileManager = FileManager.default
                do {
                    try fileManager.removeItem(at: URL(string: signupData.profileImageUrl)!)
                } catch let e {
                    print(e.localizedDescription)
                }
            }
        }
        UserDefaults.standard.removeObject(forKey: UserDefaultsKey.signupData.rawValue)
    }
}
