package com.cocotalk.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTime implements Principal {
    private Long id;

    @NotNull
    private String cid; // 코코톡 아이디

    @NotNull
    private String password;

    private String email;

    @NotNull
    private String name;

    @NotNull
    private String nickname;

    @NotNull
    private String phone;

    @NotNull
    private Provider provider;

    @NotNull
    private String providerId;

    private String profile; // JSON 형태로 저장

    @NotNull
    private Short status; // 유저 상태

    private LocalDateTime loggedinAt; // 최종 접속 기록

    private Date birth;
}
