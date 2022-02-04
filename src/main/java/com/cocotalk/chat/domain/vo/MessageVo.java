package com.cocotalk.chat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVo<T> {
    private T chatMessage;
    private BundleIdVo bundleId;
}
