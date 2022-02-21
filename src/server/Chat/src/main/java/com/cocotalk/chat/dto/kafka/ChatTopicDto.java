package com.cocotalk.chat.dto.kafka;

import lombok.*;

import javax.validation.constraints.NotNull;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatTopicDto<T> {
    @NotNull
    private String send; // 웹소켓 pub 주소
    @NotNull
    private T payload; // 보낼 내용
}
