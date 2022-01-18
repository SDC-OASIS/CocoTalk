package com.cocotalk.dto.email.issue;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class IssueInput {
    @NotBlank
    private String email;
}