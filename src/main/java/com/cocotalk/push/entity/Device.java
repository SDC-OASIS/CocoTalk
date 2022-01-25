package com.cocotalk.push.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
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

    @Column("os")
    private String os;

    @Column("type")
    private Short type;

    @Column("loggedin_at")
    private LocalDateTime loggedinAt;

}

