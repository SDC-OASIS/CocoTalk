package com.cocotalk.chat.repository;

import com.cocotalk.chat.domain.entity.message.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, ObjectId>, QuerydslPredicateExecutor<ChatMessage> {
    Optional<ChatMessage> findById(ObjectId id);
}
