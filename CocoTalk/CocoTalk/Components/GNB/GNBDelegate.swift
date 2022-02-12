//
//  GNBDelegate.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/11.
//

import Foundation

protocol GNBDelegate: AnyObject {
    /// 친구 추가 버튼 클릭할 때
    func tapAddFriend()
    
    /// 채팅 생성 버튼 클릭할 때
    func tapAddChat()
    
    /// QR 버튼 클릭할 때
    func tapQR()
    
    /// 탭 타입
    func gnbTabType() -> TabEnum
    
    /// 검색 버튼 클릭할 때
    func tapSearch()
    
    /// 설정 버튼 클릭할 때
    func tapSetting()
}

extension GNBDelegate {
    func tapAddFriend() {}
    
    func tapAddChat() {}
    
    func tapQR() {}
}
