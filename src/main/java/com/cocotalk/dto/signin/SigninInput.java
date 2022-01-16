package com.cocotalk.dto.signin;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class SigninInput {
    private String cid;
    private String password;
}
