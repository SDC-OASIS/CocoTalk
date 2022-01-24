package com.cocotalk.auth.dto.signup;

import com.cocotalk.auth.entity.Provider;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SignupOutput {
    private String cid; // 코코톡 아이디
    private String name;
    private String nickname;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate birth;
    private String phone;
    private String email;
    @Enumerated(EnumType.STRING)
    private Provider provider;
    private String providerId;
}
