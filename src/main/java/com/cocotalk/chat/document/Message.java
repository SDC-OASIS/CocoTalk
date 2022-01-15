package com.cocotalk.chat.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "message")
public class Message {
    @Id
    private String id;

    @DBRef(lazy = true)
    private Room room;

    private Long userId;

    private Short type;

    private String content;

    private LocalDateTime sentAt;
}
