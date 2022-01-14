package com.cocotalk.chat.repository;

import com.cocotalk.chat.document.RoomMember;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomMemberRepository extends ReactiveMongoRepository<RoomMember, String>, ReactiveQuerydslPredicateExecutor<RoomMember> {
}
