package com.cocotalk.push.dto.push;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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
