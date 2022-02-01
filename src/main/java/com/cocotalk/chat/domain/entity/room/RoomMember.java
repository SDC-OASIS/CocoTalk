package com.cocotalk.chat.domain.entity.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMember {
    private Long userId; // MySQL userId

    private boolean joining;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (!(obj instanceof RoomMember)) return false;
        RoomMember roomMember = (RoomMember) obj;
        return Objects.equals(userId, roomMember.userId);
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public void setLeftAt(LocalDateTime leftAt) {
        this.leftAt = leftAt;
    }
}
