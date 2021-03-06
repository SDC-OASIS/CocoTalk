package com.cocotalk.chat.dto.kafka;

import com.cocotalk.chat.domain.entity.message.MessageType;
import com.cocotalk.chat.domain.entity.room.RoomType;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *
 * kafka의 push topic에서 받아 올 push 요청의 dto 입니다.
 * push 서버에서 동일한 형태로 kafka의 push topic에 sub을 하고 있습니다.
 *
 */
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushTopicDto {
    @NotNull
    private List<Long> userIdList; //push를 받을 user의 id list
    @NotNull
    private String roomId;
    @NotNull
    private String roomname;
    @NotNull
    private RoomType roomType;
    @NotNull
    private String username;
    @NotNull
    private String message;
    @NotNull
    private MessageType messageType;
}
