package com.cocotalk.push.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    @NotBlank
    private String targetToken;
    @NotNull
    private String title;
    @NotNull
    private String body;
}
