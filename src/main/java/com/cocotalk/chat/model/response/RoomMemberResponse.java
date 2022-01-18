package com.cocotalk.chat.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMemberResponse {
    private Long userId; // MySQL userId

    private Boolean isJoining;

    private LocalDateTime accessedAt;

    private LocalDateTime joinedAt;
}
