package com.cocotalk.chat.repository;

import com.cocotalk.chat.document.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, String>, QuerydslPredicateExecutor<Message> {
}
