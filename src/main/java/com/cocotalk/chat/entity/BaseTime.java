package com.cocotalk.chat.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
`
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTime {
    @CreatedDate
    @Column(name = "createdAt")
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modifiedAt")
    protected LocalDateTime modifiedAt; // 패스워드 변경 기능 염두
}