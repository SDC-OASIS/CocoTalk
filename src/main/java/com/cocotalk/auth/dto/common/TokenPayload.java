package com.cocotalk.auth.dto.common;

import lombok.*;

import javax.validation.constraints.NotBlank;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenPayload {
    private Long userId;
    private String fcmToken;
}
