package com.cocotalk.gateway.dto;

import lombok.Getter;

@Getter
public class TokenPayload {
    private Long userId;
    private String fcmToken;
}
