package com.cocotalk.chat.repository;

import com.cocotalk.chat.domain.entity.message.MessageBundle;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

@RequiredArgsConstructor
public class CustomBundleRepositoryImpl implements CustomBundleRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public MessageBundle findBundleAndSlice(ObjectId messageBundleId, int start, int unit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(messageBundleId));
        query.fields().slice("messageIds", start, unit);
        return mongoTemplate.findOne(query, MessageBundle.class);
    }

    @Override
    public Optional<MessageBundle> findBeforeBundleAndSlice(ObjectId roomId, ObjectId currentMessageBundleId, int start, int diff) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId));
        query.addCriteria(Criteria.where("_id").lt(currentMessageBundleId));
        query.fields().slice("messageIds", start, diff);
        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        query.limit(1);
        return Optional.ofNullable(mongoTemplate.findOne(query, MessageBundle.class));
    }
}
