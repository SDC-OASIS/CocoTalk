package com.cocotalk.chat.dto.request;

import com.cocotalk.chat.domain.vo.MessageVo;
import com.cocotalk.chat.domain.vo.RoomVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageWithRoomRequest {
    private ChatMessageRequest chatMessageRequest;
    private RoomRequest roomRequest;
}
