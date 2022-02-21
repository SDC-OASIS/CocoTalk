package com.cocotalk.chat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMemberVo {
    private Long userId;

    private String userName;

    private String profile;

    private boolean joining; // 채팅방에 참가 중인지 여부

    private LocalDateTime joinedAt; // 채팅방에 입장한 시간

    private LocalDateTime enteredAt; // 채팅방 소켓을 connect한 시간

    private LocalDateTime awayAt; // 채팅방 소켓을 disconnect한 시간

    private LocalDateTime leftAt; // 채팅방에서 나간 시간
}
