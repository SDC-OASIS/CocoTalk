package com.cocotalk.user.dto.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserModifyRequest {
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private LocalDate birth;
    private Short status;
}
