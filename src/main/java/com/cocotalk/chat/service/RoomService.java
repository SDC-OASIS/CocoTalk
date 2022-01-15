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

    public Optional<Room> findPrivateRoom(String me, String friend){
        Predicate myNamePredicate = qRoom.memberName.contains(me);
        Predicate friendNamePredicate = qRoom.memberName.contains(friend);
        Predicate predicate = ((BooleanExpression) myNamePredicate).and(
                friendNamePredicate);
        return roomRepository.findOne(predicate);
    }

    public Room createPrivateRoom(Room privateRoom){
        return roomRepository.save(privateRoom);
    }
}
