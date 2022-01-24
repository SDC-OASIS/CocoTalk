package com.cocotalk.auth.dto.email.validation;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ValidationOutput {
    private Boolean isValid;
}