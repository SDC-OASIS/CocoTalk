package com.cocotalk.auth.dto.signup;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class SignupInput {
    @NotBlank
    private String cid; // 코코톡 아이디
    @NotBlank
    private String password;
    @NotBlank
    private String username;
    @NotBlank
    private String nickname;
    private LocalDate birth;
    @NotBlank
    private String phone;
    @NotBlank
    private String email;
    @NotNull
    private Short status; // 유저 상태
    private MultipartFile profileImg;
    private MultipartFile profileImgThumb;
}

