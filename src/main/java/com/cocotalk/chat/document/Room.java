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

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "room")
public class Room {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private String name;

    private String img;

    private int type; // 0=갠톡, 1=단톡, 2=오픈톡

    private List<RoomMember> members;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> messageIds;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> noticeIds;
}
