package com.cocotalk.chat.domain.entity.message;

public enum MessageType {
    CHAT, // 0, 일반 메시지
    JOIN, // 1, XX님이 들어왔습니다.
    LEFT, // 2, XX님이 나갔습니다.
    INVITE, // 3. XX님이 초대됐습니다.
    AWAY, // 4, Socket Disconnect TEST용
    AWAKE // 5, 갠톡에서 상대방 나갔을 때 isJoining = false -> true로 변경 트리거
}
