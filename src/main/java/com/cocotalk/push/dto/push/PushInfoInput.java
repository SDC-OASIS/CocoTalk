package com.cocotalk.push.dto.push;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushInfoInput {
    @NotNull
    private List<String> tokenList;
    @NotNull
    private String title;
    @NotNull
    private String body;
}
