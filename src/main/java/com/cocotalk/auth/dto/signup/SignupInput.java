package com.cocotalk.auth.dto.signup;

import com.sun.istack.NotNull;
import lombok.*;

import javax.validation.constraints.NotBlank;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SignupInput {
    @NotBlank
    private String cid; // 코코톡 아이디
    @NotBlank(message = "비밀번호는 필수값 입니다.")
    private String password;
    @NotBlank
    private String userName;
    @NotBlank
    private String nickname;
    private LocalDate birth;
    @NotBlank
    private String phone;
    @NotBlank(message = "이메일은 필수값 입니다.")
    private String email;
    @NotNull
    private Short status; // 유저 상태
    private String profile; // JSON 형태로 저장
}
