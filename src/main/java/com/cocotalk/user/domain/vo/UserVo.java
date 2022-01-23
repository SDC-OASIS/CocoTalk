package com.cocotalk.user.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {
    private Long id;
    private String cid;
    private String name;
    private String nickname;
    private LocalDate birth;
    private String phone;
    private String email;
    private Short status;
    private LocalDateTime loggedinAt;
    private String profile;
}
