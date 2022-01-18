package com.cocotalk.chat.document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "message")
public class ChatMessage {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId roomId;

    private Long userId;

    private int type;

    private String content;

    private LocalDateTime sentAt;
}
