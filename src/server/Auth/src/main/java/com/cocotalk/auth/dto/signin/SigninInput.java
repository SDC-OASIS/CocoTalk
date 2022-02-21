package com.cocotalk.auth.dto.signin;

import lombok.*;

import javax.validation.constraints.NotBlank;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class SigninInput {
    @NotBlank
    private String cid;
    @NotBlank
    private String password;
    @NotBlank
    private String fcmToken; // 로그인 시에 Push 서버에게 요청해 디바이스의 fcmToken을 갱신합니다.
}
