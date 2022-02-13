//
//  SocketTopicEnum.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/12.
//

import Foundation

enum SocketTopicEnum {
    /// 채팅방 밖에서 사용할 메시지 토픽
    case listMessage
    
    /// 채팅방 목록을 토픽
    case listRoomInfo
    
    /// 새로운 채팅방  토픽
    case listNewRoom
    
    /// 동시 로그인 시 로그아웃 시키기 위한 토픽
    case listCrash
    
    /// 채팅방 안에서 사용할 메시지 토픽
    case chatMessage
    
    /// 채팅방 안에서 사용할 채팅방 정보 토픽
    case chatRoomInfo
}
