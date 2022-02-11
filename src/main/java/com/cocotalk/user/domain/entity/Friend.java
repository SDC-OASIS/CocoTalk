package com.cocotalk.user.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

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
    private User fromUser; // 친구 추가한 유저

    @ManyToOne
    @JoinColumn(name = "to_uid")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User toUser; // 친구 추가된 유저

    @NotNull
    private boolean hidden;

    public void setHidden(boolean hide) {
        this.hidden = hide;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (!(obj instanceof Friend)) return false;
        Friend friend = (Friend) obj;
        return Objects.equals(friend.toUser.getId(), toUser.getId());
    }
}
