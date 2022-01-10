package com.cocotalk.chat.entity;

import com.cocotalk.chat.entity.BaseTime;
import com.cocotalk.chat.entity.Provider;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.security.auth.Subject;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

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
