package com.cocotalk.chat.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomListVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private String name;

    private String img;

    private int type;

    private ChatMessageVo lastChatMessageVo;

    private Long unreadNumber;
}
