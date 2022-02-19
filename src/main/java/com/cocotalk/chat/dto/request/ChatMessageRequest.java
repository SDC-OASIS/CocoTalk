package com.cocotalk.chat.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId roomId;
    private int roomType; // 푸시용

    private String roomname;

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId messageBundleId;

    private Long userId;

    private String username; // 푸시용

    private List<Long> receiverIds;

    private String content;

    private int type; // 메시지 타입
}
