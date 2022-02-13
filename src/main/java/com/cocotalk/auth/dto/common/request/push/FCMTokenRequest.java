package com.cocotalk.auth.dto.common.request.push;


import lombok.*;

/**
 *
 * push server의 api를 호출할 때 사용하는 dto
 *
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FCMTokenRequest {
    private Long userId;
    private String fcmToken;
}
