package com.cocotalk.user.dto.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserModifyRequest {
    private String userName;
    private String nickname;
    private String email;
    private String phone;
    private String profile;
    private LocalDate birth;
    private Short status;
}
