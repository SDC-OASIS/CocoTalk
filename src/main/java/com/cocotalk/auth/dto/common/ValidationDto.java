package com.cocotalk.auth.dto.common;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ValidationDto {
    private Boolean isValid;
}