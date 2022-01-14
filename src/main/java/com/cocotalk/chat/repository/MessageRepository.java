package com.cocotalk.chat.repository;

import com.cocotalk.chat.document.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends ReactiveMongoRepository<Message, String>, ReactiveQuerydslPredicateExecutor<Message> {
}
