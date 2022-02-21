package com.cocotalk.chat.repository;

import com.cocotalk.chat.domain.entity.room.Room;
import com.cocotalk.chat.domain.entity.room.RoomType;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CustomRoomRepositoryImpl implements CustomRoomRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<Room> findPrivate(Long userId, Long friendId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("type").is(RoomType.PRIVATE.ordinal()));
        query.addCriteria(Criteria.where("members").size(2)
                .elemMatch(Criteria.where("userId").is(userId))
                .andOperator(Criteria.where("members").elemMatch(Criteria.where("userId").is(friendId))));
        return Optional.ofNullable(mongoTemplate.findOne(query, Room.class));
    }

    @Override
    public List<Room> findJoiningRoom(Long userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("members").elemMatch(Criteria.where("userId").is(userId).and("joining").is(true)));
        return mongoTemplate.find(query, Room.class);
    }

    @Override
    public Long updateUsername(Long userId, String username) {
        Query query = new Query();
        Update update = new Update();

        query.addCriteria(Criteria.where("members")
                .elemMatch(Criteria.where("userId").is(userId)));

        update.set("members.$.username", username);
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, "rooms");

        log.info(updateResult.toString());

        return updateResult.getModifiedCount();
    }

    @Override
    public Long updateProfile(Long userId, String profile) {
        Query query = new Query();
        Update update = new Update();

        query.addCriteria(Criteria.where("members")
                .elemMatch(Criteria.where("userId").is(userId)));

        update.set("members.$.profile", profile);
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, "rooms");

        log.info(updateResult.toString());

        return updateResult.getModifiedCount();
    }
}
