package com.cocotalk.auth.dto.common;

import lombok.*;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DeviceDto {
    private Long id;
    private String token;
    private Long userId;
    private String ip;
    private String agent;
    private Short type;
    private LocalDateTime loggedinAt;
}

