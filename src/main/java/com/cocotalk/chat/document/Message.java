package com.cocotalk.chat.document;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Builder
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
