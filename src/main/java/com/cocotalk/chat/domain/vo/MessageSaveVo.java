package com.cocotalk.chat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSaveVo {
    private RoomVo roomVo;
    private MessageBundleVo messageBundleVo;
}
