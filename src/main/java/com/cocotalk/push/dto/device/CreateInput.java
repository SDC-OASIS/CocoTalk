package com.cocotalk.push.dto.device;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
public class CreateInput {
    @NonNull
    private Long userId;
    @NonNull
    private String fcmToken;
}
