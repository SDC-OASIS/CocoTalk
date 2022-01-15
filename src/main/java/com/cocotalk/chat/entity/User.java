package com.cocotalk.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.security.auth.Subject;
import java.security.Principal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Principal {
    private String name;

    @Override
    public boolean implies(Subject subject) {
        return Principal.super.implies(subject);
    }

    @Override
    public String getName() {
        return name;
    }
}
