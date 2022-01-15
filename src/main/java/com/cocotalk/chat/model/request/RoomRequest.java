package com.cocotalk.chat.model.request;

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
    private List<String> memberName;
    private List<ObjectId> memberPk;
    private List<ObjectId> messagePk;
    private List<ObjectId> noticePk;
}
