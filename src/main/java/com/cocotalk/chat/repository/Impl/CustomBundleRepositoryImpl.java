package com.cocotalk.chat.repository.Impl;

import com.cocotalk.chat.domain.entity.message.MessageBundle;
import com.cocotalk.chat.repository.CustomBundleRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomBundleRepositoryImpl implements CustomBundleRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<MessageBundle> findJustBeforeAndSlice(ObjectId roomId, ObjectId currentMessageBundleId, int start, int diff) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId));
        query.addCriteria(Criteria.where("_id").lt(currentMessageBundleId));
        query.fields().slice("messageIds", start, diff);
        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        query.limit(1);
        return Optional.ofNullable(mongoTemplate.findOne(query, MessageBundle.class));
    }
}
