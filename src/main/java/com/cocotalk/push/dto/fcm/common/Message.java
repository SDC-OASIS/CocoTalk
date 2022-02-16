package com.cocotalk.push.dto.fcm.common;

import com.cocotalk.push.dto.fcm.FCMMessage;
import com.cocotalk.push.dto.fcm.ios.Apns;
import com.cocotalk.push.dto.fcm.web.Webpush;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class Message {
    private String token; // 특정 device에 알림을 보내기위해 사용
    private Data data;
    private Apns apns;
    private Webpush webpush;
}