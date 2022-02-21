package com.cocotalk.chat.dto;

import lombok.Getter;

@Getter
public class TokenPayload {
    private Long userId;
    private String fcmToken;
}
