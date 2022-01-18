package com.cocotalk.chat.repository;

import com.cocotalk.chat.document.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String>, QuerydslPredicateExecutor<ChatMessage> {
}
