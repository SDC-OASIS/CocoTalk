package com.cocotalk.user.domain.vo;

import com.cocotalk.user.domain.entity.Provider;

import java.time.LocalDateTime;
import java.util.Date;

public class UserVo {
    private Long id;
    private String cid;
    private String password;
    private String name;
    private String nickname;
    private Date birth;
    private String phone;
    private String email;
    private Provider provider;
    private String providerId;
    private Short status;
    private LocalDateTime loggedinAt;
    private String profile;
}
