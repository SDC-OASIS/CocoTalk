package com.cocotalk.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMemberRequest {
    private Long userId;

    private String username;

    private String profile;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (!(obj instanceof RoomMemberRequest)) return false;
        RoomMemberRequest roomMemberRequest = (RoomMemberRequest) obj;
        return Objects.equals(userId, roomMemberRequest.getUserId());
    }
}
