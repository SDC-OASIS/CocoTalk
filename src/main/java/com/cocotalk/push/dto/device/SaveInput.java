package com.cocotalk.push.dto.device;

import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@ToString
@Getter
@Setter
@NoArgsConstructor
public class SaveInput {
    @NotNull
    private Long userId;
    @NotBlank
    private String fcmToken;
}
