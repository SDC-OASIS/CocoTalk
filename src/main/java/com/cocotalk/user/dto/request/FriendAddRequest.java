package com.cocotalk.user.dto.request;

import lombok.Getter;

@Getter
public class FriendAddRequest {
    Long fromUid;
    Long toUid;
}
