package com.cocotalk.user.domain.vo;

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
