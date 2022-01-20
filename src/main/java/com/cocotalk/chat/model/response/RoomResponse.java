package com.cocotalk.chat.model.response;

import com.cocotalk.chat.document.room.RoomMember;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private String name;

    private String img;

    private int type;

    private List<RoomMember> members;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> messageIds;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> noticeIds;
}
