package com.cocotalk.chat.repository;

import com.cocotalk.chat.document.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends MongoRepository<Room, String>, QuerydslPredicateExecutor<Room> {
}
