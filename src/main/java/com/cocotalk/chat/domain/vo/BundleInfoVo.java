package com.cocotalk.chat.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BundleInfoVo {
    private int currentMessageBundleCount; // 현재 메시지 번들에 쌓인 메시지 Id의 갯수

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId currentMessageBundleId; // 현재 이 메시지가 저장된 메시지 번들의 Id

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId nextMessageBundleId; // 다음 메시지가 저장될 메시지 번들의 Id
}
