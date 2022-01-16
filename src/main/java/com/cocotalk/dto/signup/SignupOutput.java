package com.cocotalk.dto.signup;

import com.cocotalk.entity.Provider;
import com.cocotalk.entity.User;
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

    public static SignupOutput toDto(User user){
        return SignupOutput.builder()
                .cid(user.getCid())
                .name(user.getName())
                .nickname(user.getNickname())
                .birth(user.getBirth())
                .phone(user.getPhone())
                .email(user.getEmail())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .build();

    }
}
