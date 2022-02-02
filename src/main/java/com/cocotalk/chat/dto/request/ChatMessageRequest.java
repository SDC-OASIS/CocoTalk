package com.cocotalk.chat.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId roomId;

    private Long userId;

    private int type;

    private String content;

    private LocalDateTime sentAt;
}
