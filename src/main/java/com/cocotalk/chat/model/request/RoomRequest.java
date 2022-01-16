package com.cocotalk.chat.model.request;

import com.cocotalk.chat.document.RoomMember;
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
public class RoomRequest {
    private String name;
    private String img;
    private Short type;
    private List<RoomMember> members;
    private List<ObjectId> messagePk;
    private List<ObjectId> noticePk;
}
