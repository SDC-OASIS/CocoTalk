package com.cocotalk.chat.repository;

import com.cocotalk.chat.document.room.RoomMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomMemberRepository extends MongoRepository<RoomMember, String>, QuerydslPredicateExecutor<RoomMember> {
}
