package com.cocotalk.auth.dto.common.request.chat;

import lombok.*;

/**
 *
 * chat server의 api를 호출할 때 사용하는 dto
 *
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrashRequest {
    private String clientType;
    private Long userId;
    private String fcmToken;
}
