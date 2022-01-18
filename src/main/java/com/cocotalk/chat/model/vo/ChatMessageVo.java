package com.cocotalk.chat.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private Long userId;

    private int type;

    private String content;

    private LocalDateTime sentAt;
}
