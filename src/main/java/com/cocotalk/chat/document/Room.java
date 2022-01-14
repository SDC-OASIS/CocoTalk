package com.cocotalk.chat.document;

import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Document(collection = "room")
public class Room {
    @Id
    private String id;

    private String title;

    private String img;

    private Short type; // 0=갠톡, 1=단톡, 2=오픈톡

    private List<ObjectId> memberPk = new ArrayList<>();

    private List<ObjectId> messagePk = new ArrayList<>();

    private List<ObjectId> noticePk = new ArrayList<>();
}
