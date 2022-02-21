package com.cocotalk.auth.dto.common.payload;

import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 *
 * access token, refresh token의 안에 들어가는 payload
 *
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenPayload {
    private Long userId;
    private String fcmToken;
}
