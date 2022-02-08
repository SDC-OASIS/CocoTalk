package com.cocotalk.auth.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
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

    @NotNull
    @Column(length = 20)
    private String cid; // 코코톡 아이디

    @NotNull
    @Column(length = 64)
    private String password;

    @NotNull
    @Column(length = 20)
    private String username;

    @NotNull
    @Column(length = 20)
    private String nickname;

    @Column(length = 125, unique = true)
    private String email;

    @NotNull
    @Column(length = 20, unique = true)
    private String phone;

    private String profile; // JSON 형태로 저장

    @NotNull
    private Short status; // 유저 상태

    private LocalDate birth;

    @Column(name = "loggedin_at")
    private LocalDateTime loggedinAt; // 최종 접속 기록



}