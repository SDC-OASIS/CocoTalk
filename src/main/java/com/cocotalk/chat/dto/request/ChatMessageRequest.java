package com.cocotalk.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    private Long userId;

    private int type;

    private String content;

    private LocalDateTime sentAt;
}
