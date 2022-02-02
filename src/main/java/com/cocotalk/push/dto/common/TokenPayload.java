package com.cocotalk.push.dto.common;

import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenPayload {
    private Long userId;
    private String fcmToken;
}
