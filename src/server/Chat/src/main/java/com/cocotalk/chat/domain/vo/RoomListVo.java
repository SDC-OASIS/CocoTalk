package com.cocotalk.chat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomListVo {
    private RoomVo room;

    private ChatMessageVo recentChatMessage; // 이 방에서의 가장 최근 메시지

    private int recentMessageBundleCount; // 현재 가장 최신 메시지 번들의 count 수

    private Long unreadNumber; // 이 방에서 읽지 않은 메시지의 수
}
