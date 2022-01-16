package com.cocotalk.chat.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "room")
public class Room {
    @Id
    private String id;

    private String name;

    private String img;

    private Short type; // 0=갠톡, 1=단톡, 2=오픈톡

    private List<RoomMember> members;

    private List<ObjectId> messagePk;

    private List<ObjectId> noticePk;
}
