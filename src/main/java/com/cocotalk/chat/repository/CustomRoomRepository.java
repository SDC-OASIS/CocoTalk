package com.cocotalk.chat.repository;

import com.cocotalk.chat.domain.entity.room.Room;

import java.util.List;
import java.util.Optional;

public interface CustomRoomRepository {
    Optional<Room> findPrivate(Long userId, Long friendId);
    List<Room> findJoiningRoom(Long userId);
}
