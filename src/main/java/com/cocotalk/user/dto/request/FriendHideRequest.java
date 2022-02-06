package com.cocotalk.user.dto.request;

import lombok.Getter;

@Getter
public class FriendHideRequest {
    Long toUid;
    boolean hidden;
}