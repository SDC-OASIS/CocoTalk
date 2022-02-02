package com.cocotalk.push.dto.device;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class DeleteInput {
    @NonNull
    private Long userId;
}
