package com.cocotalk.chat.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CrashRequest {
    private String clientType;
    private Long userId;
    private String fcmToken;
}
