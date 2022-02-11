//
//  ModelProfile.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/17.
//

import Foundation

struct ModelProfile: Codable {
    var id: Int?
    var cid: String?
    var username: String?
    var nickname: String?
    var birth: String?
    var profile: String?
    var phone: String?
    var email: String?
    var loggedinAt: String?
    var status: Int?
    
    var bio: String?
    var profileImageURL: String?
    var bgImageURL: String?
    
    #warning("CoreData로 확인하기")
    /// CoreData에 업데이트가 생겼을 경우
    /// 1. 프로필 수정 시간 최신화
    /// 2. 뉴 프로필 true (24시간 안에 봤을 경우)
    var isNewProfile: Bool?
}


#warning("주석 삭제")
/*

 - 아이디로 친구 추가
 - 해당 친구의 전화번호는 알 수 없어야 맞다.
 - 우리 서비스는 전화번호 등록이 되기 때문에 전화번호가 리스폰스로 전달된다.
 - 연락처에 있는 사람이면 전화번호가 표시되고, 그렇지 않으면 전화번호는 표시되지 않는다.
 
 */
