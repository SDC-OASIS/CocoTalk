package com.cocotalk.chat.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresenceRequest {
    private String action;
    private String serverUrl;
}
