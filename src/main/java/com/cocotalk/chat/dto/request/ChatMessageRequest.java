package com.cocotalk.chat.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId roomId;

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId messageBundleId;

    private Long userId;

    private String content;

    private int type;
}
