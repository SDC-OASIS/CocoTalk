package com.cocotalk.chat.domain.vo;

import com.cocotalk.chat.domain.entity.room.RoomMember;
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
public class RoomVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private String roomname;

    private String img;

    private int type; // 0=갠톡, 1=단톡, 2=오픈톡

    private List<RoomMember> members;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> messageBundleIds;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> noticeIds;
}
