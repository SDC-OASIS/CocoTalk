package com.cocotalk.chat.document;

import com.mongodb.DBRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notice")
public class Notice {
    @Id
    private String id;

    private DBRef room;

    private String title;

    private String content;
}
