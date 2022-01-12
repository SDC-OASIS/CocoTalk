package com.cocotalk.user.dto.request;

import lombok.Getter;

import java.util.Date;

@Getter
public class UserModifyRequest {
    private String name;
    private String nickname;
    private Date birth;
    private String phone;
    private String email;
    private Short status;
    private String profile;
}
