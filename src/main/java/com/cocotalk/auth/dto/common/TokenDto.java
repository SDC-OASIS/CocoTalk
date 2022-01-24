package com.cocotalk.auth.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class TokenDto {
    private String accessToken;
    private String refreshToken;
}
