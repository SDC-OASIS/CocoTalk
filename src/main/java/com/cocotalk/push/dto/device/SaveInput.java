package com.cocotalk.push.dto.device;

import lombok.*;


@ToString
@Getter
@Setter
@NoArgsConstructor
public class SaveInput {
    @NonNull
    private Long userId;
    @NonNull
    private String fcmToken;
}
