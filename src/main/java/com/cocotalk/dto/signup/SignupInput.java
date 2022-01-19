package com.cocotalk.dto.signup;

import com.cocotalk.entity.Provider;
import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SignupInput {
    @NotBlank
    private String cid; // 코코톡 아이디
    @NotBlank(message = "비밀번호는 필수값 입니다.")
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String nickname;
    private LocalDate birth;
    @NotBlank
    private String phone;
    @NotBlank(message = "이메일은 필수값 입니다.")
    private String email;
    @NotNull
    private String provider;
    @NotBlank
    private String providerId;
    @NotNull
    private Short status; // 유저 상태
    private String profile; // JSON 형태로 저장
}
