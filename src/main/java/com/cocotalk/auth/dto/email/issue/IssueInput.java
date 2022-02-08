package com.cocotalk.auth.dto.email.issue;

import lombok.*;

import javax.validation.constraints.NotBlank;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class IssueInput {
    @NotBlank
    private String email;
}