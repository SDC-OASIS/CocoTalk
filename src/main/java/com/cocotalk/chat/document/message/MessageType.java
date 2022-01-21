package com.cocotalk.chat.document.message;

public enum MessageType {
    CHAT, // 0, 일반 메시지
    JOIN, // 1, XX님이 들어왔습니다.
    LEFT, // 2, XX님이 나갔습니다.
    AWAY, // 3, Socket Disconnect TEST용
    AWAKE // 4, 갠톡에서 상대방 나갔을 때 isJoining = false -> true로 변경 트리거
}
