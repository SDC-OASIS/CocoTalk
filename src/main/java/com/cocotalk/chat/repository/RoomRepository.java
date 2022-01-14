package com.cocotalk.chat.repository;

import com.cocotalk.chat.document.Room;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends ReactiveMongoRepository<Room, String>, ReactiveQuerydslPredicateExecutor<Room> {
}
