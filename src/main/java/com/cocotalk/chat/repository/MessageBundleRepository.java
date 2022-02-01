package com.cocotalk.chat.repository;

import com.cocotalk.chat.domain.entity.message.MessageBundle;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageBundleRepository extends MongoRepository<MessageBundle, ObjectId>, QuerydslPredicateExecutor<MessageBundle> {
}
