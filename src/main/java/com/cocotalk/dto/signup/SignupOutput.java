package com.cocotalk.dto.signup;

import com.cocotalk.entity.Provider;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SignupOutput {
    private String cid; // 코코톡 아이디
    private String name;
    private String nickname;
    private Date birth;
    private String phone;
    private String email;
    private Provider provider;
    @Enumerated(EnumType.STRING)
    private String providerId;
}
