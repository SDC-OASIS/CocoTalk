package com.cocotalk.chat.repository;

import com.cocotalk.chat.domain.entity.room.Room;
import com.cocotalk.chat.domain.entity.room.RoomType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomRoomRepositoryImpl implements CustomRoomRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<Room> findPrivate(Long userId, Long friendId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("type").is(RoomType.PRIVATE.ordinal()));
        query.addCriteria(Criteria.where("members").elemMatch(Criteria.where("userId").is(userId))
                .andOperator(Criteria.where("members").elemMatch(Criteria.where("userId").is(friendId))));
        return Optional.ofNullable(mongoTemplate.findOne(query, Room.class));
    }

    @Override
    public List<Room> findJoiningRoom(Long userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("members").elemMatch(Criteria.where("userId").is(userId).and("joining").is(true)));
        return mongoTemplate.find(query, Room.class);
    }
}
