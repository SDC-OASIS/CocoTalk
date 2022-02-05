package com.cocotalk.chat.repository;

import com.cocotalk.chat.domain.entity.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@RequiredArgsConstructor
public class CustomRoomRepositoryImpl implements CustomRoomRepository {
    private final MongoTemplate mongoTemplate;

//    @Override
//    public Optional<Room> findPrivate(Long userId, Long friendId) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("type").is(RoomType.PRIVATE.ordinal()));
//        query.addCriteria(Criteria.where("members").elemMatch(Criteria.where("userId").in(userId))
//                .andOperator(Criteria.where("members").elemMatch(Criteria.where("userId").in(friendId)))
//                .andOperator(Criteria.where("type").is(RoomType.PRIVATE.ordinal())));
//        return Optional.ofNullable(mongoTemplate.findOne(query, Room.class));
//    }

    @Override
    public List<Room> findJoiningRoom(Long userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("members")
                .elemMatch(Criteria.where("userId").is(userId))
                .elemMatch(Criteria.where("joining").is(true)));
        return mongoTemplate.find(query, Room.class);
    }
}
