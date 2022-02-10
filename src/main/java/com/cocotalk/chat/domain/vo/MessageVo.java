package com.cocotalk.chat.domain.vo;

import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVo<T> {
    private T message;
    private BundleInfoVo bundleInfo;
}
