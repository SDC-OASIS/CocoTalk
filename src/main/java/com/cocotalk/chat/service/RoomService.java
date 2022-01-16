package com.cocotalk.chat.service;

import com.cocotalk.chat.document.QRoom;
import com.cocotalk.chat.document.Room;
import com.cocotalk.chat.repository.RoomRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    QRoom qRoom = QRoom.room;

    public Room createRoom(Room room){
        return roomRepository.save(room);
    }

    public Optional<Room> findPrivateRoom(Long myid, Long friendid) {
        Predicate myIdPredicate = qRoom.members.get(0).userId.in(myid, friendid);
        Predicate friendIdPredicate = qRoom.members.get(1).userId.in(myid, friendid);
        Predicate sizePredicate = qRoom.members.size().eq(2);
        Predicate predicate = ((BooleanExpression) myIdPredicate).and(
                friendIdPredicate).and(sizePredicate);
        return roomRepository.findOne(predicate);
    }
}
