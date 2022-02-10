package com.cocotalk.chat.dto.kafka;

import com.cocotalk.chat.domain.vo.MessageVo;
import lombok.*;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatTopicDto {
    @NotNull
    private String roomId;
    @NotNull
    private MessageVo messageVo;
}
