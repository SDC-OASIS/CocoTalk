package com.cocotalk.user.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "friend", uniqueConstraints = {
        @UniqueConstraint(name = "uniqueFriend",
                columnNames = {"from_uid", "to_uid"})
})
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_uid")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_uid")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User toUser;
}
