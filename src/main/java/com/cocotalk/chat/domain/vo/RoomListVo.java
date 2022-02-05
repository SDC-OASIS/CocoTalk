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

    private ChatMessageVo recentChatMessage;

    private int recentMessageBundleCount;

    private Long unreadNumber;
}
