package com.cocotalk.dto.signup;

import com.cocotalk.entity.Provider;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter

public class SignupInput {
    @NotNull
    private String cid; // 코코톡 아이디
    @NotNull
    private String password;
    @NotNull
    private String name;
    @NotNull
    private String nickname;
    private Date birth;
    @NotNull
    private String phone;
    private String email;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Provider provider;
    @NotNull
    private String providerId;
    @NotNull
    private Short status; // 유저 상태
    private String profile; // JSON 형태로 저장
}
