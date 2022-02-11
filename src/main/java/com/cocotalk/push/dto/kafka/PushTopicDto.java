package com.cocotalk.push.dto.kafka;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushTopicDto {
    @NotNull
    private List<Long> userIdList;
    @NotNull
    private String title;
    @NotNull
    private String body;
}
