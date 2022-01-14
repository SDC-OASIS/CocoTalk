package com.cocotalk.chat.document;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Getter
@Builder
@Document(collection = "notice")
public class Notice {
    @Id
    private String id;

    @DBRef(lazy = true)
    private Room room;

    private String title;

    private String content;
}
