package com.cocotalk.push.dto.device;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SelectInput {
    @NotNull
    long userId;
}
