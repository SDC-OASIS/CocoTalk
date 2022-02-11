//
//  GNBDelegate.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/11.
//

import Foundation

@objc protocol GNBDelegate: AnyObject {
    /// 친구 추가 버튼 클릭할 때
    @objc optional func tapAddFriend()
    
    /// 채팅 생성 버튼 클릭할 때
    @objc optional func tapAddChat()
    
    /// QR 버튼 클릭할 때
    @objc optional func tapQR()
    
    /// 검색 버튼 클릭할 때
    func tapSearch()
    
    /// 설정 버튼 클릭할 때
    func tapSetting()
}
