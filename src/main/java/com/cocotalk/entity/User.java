package com.cocotalk.entity;

import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(nullable = false, length = 20, unique = true)
    private String cid; // 코코톡 아이디

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20)
    private String nickname;

    private Date birth;

    @Column(nullable = false, length = 20, unique = true)
    private String phone;

    @Column(length = 125, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(nullable = false)
    private String providerId;

    @Column(nullable = false)
    private Short status; // 유저 상태

    @Column(name = "logined_at")
    private LocalDateTime loginedAt;

    private String profile; // JSON 형태로 저장
}