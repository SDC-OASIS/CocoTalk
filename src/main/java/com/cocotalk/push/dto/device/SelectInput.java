package com.cocotalk.push.dto.device;


import com.cocotalk.push.dto.common.ClientType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SelectInput {
    @NotNull
    Long userId;
    ClientType type;
}
