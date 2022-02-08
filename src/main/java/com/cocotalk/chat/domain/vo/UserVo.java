package com.cocotalk.chat.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
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

    private String username;

    private String nickname;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate birth;

    private String phone;

    private String email;

    private Short status;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime loggedinAt;

    private String profile;
}
