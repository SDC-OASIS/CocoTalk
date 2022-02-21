package com.cocotalk.push.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@Table("device")
public class Device {
    @Id
    @Column("id")
    private Long id;

    @Column("token")
    private String token;

    @Column("user_id")
    private Long userId;

    @Column("ip")
    private String ip;

    @Column("agent")
    private String agent;

    @Column("type")
    private Short type;

    @Column("loggedin_at")
    private LocalDateTime loggedinAt;

}

