package com.cocotalk.push.dto.kafka;

public enum MessageType {
    CHAT, // 0, 일반 메시지
    INVITE, // 1. XX님이 초대됐습니다.
    LEFT, // 2, XX님이 나갔습니다.
    AWAKE, // 3, 갠톡에서 상대방 나갔을 때 isJoining = false -> true로 변경 트리거
    PHOTO, // 4. 사진 메시지
    VIDEO, // 5. 동영상 메시지
    FILE, // 6. 그 외 파일
}
