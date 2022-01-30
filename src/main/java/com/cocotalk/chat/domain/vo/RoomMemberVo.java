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

    private Boolean isJoining;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;
}
