package com.cocotalk.chat.document.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "room_member")
public class RoomMember {
    private Long userId; // MySQL userId

    private Boolean isJoining; // 갠톡에서만 사용

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (!(obj instanceof RoomMember)) return false;
        RoomMember roomMember = (RoomMember) obj;
        return Objects.equals(userId, roomMember.userId);
    }
}
