package com.cocotalk.push.dto.fcm.ios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class Aps {
    Alert alert;
    String sound;
}