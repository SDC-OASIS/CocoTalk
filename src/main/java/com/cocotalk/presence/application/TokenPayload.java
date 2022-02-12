package com.cocotalk.presence.application;

import lombok.Getter;

@Getter
public class TokenPayload {
    private Long userId;
    private String fcmToken;
}
