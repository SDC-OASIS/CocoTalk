package com.cocotalk.push.dto.fcm.ios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class Apns {
    Payload payload;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Payload {
        Aps aps;
    }
}
