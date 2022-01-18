package com.cocotalk.dto.email.issue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class IssueOutput {
    private Date expirationDate;
}