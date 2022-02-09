package com.cocotalk.push.dto.device;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
public class DeleteInput {
    @NotNull
    private Long userId;
}
