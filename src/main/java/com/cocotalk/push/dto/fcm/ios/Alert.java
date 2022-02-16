package com.cocotalk.push.dto.fcm.ios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class Alert {
    String title;
    String subtitle;
    String body;
}
