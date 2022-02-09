package com.cocotalk.auth.dto.common;


import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FCMTokenDto {
    private Long userId;
    private String fcmToken;
}
