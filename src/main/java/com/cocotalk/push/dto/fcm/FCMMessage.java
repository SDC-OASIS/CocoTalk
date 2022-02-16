package com.cocotalk.push.dto.fcm;

import com.cocotalk.push.dto.fcm.common.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FCMMessage {
    private boolean validate_only;
    private Message message;
}
