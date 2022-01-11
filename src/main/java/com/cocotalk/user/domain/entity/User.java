package com.cocotalk.user.domain.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
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

    @NotNull
    @Column(length = 20)
    private String cid; // 코코톡 아이디

    @NotNull
    @Column(length = 64)
    private String password;

    @NotNull
    @Column(length = 20)
    private String name;

    @NotNull
    @Column(length = 20)
    private String nickname;

    private Date birth;

    @NotNull
    @Column(length = 13, unique = true)
    private String phone;

    @Column(length = 40, unique = true)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @NotNull
    private String providerId;

    @NotNull
    private Short status; // 유저 상태
    
    private LocalDateTime loginedAt; // 최종 접속 기록

    private String profile; // JSON 형태로 저장
}
