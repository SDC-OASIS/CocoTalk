package com.cocotalk.chat.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "room_member")
public class RoomMember {
    @Id
    private String id;

    @DBRef(lazy = true)
    private Room room;

    private Long userId;

    private LocalDateTime accessedAt;

    private LocalDateTime joinedAt;

    private Boolean isJoining;
}
