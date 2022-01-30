package com.cocotalk.chat.repository;

import com.cocotalk.chat.domain.entity.room.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends MongoRepository<Room, String>, QuerydslPredicateExecutor<Room> {
    List<Room> findByMembersUserId(Long userId);
}
