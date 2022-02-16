package com.cocotalk.push.dto.fcm.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class Webpush {
    Data data;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data {
        String title;
        String body;
        String url;
    }
}

