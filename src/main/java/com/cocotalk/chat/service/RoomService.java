package com.cocotalk.chat.service;

import com.cocotalk.chat.document.QRoom;
import com.cocotalk.chat.document.room.Room;
import com.cocotalk.chat.document.room.RoomMember;
import com.cocotalk.chat.document.message.ChatMessage;
import com.cocotalk.chat.document.message.MessageType;
import com.cocotalk.chat.model.exception.GlobalError;
import com.cocotalk.chat.model.exception.GlobalException;
import com.cocotalk.chat.repository.RoomRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    QRoom qRoom = QRoom.room;

    public Room save(Room room) {
        return roomRepository.save(room);
    }

    public Room find(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new GlobalException(GlobalError.BAD_REQUEST, "존재하지 않는 채팅방입니다."));
    }

    public List<Room> findAll(Long userId) {
        return roomRepository.findByMembersUserId(userId);
    }

    public RoomMember findMember(Room room, Long userId) {
        return room.getMembers().stream()
                .filter(roomMember -> userId.equals(roomMember.getUserId()))
                .findFirst()
                .orElseThrow(() -> new GlobalException(GlobalError.BAD_REQUEST, "채팅방에 속해 있지 않은 유저입니다."));
    }

    public Room findPrivate(Long userid, Long friendid) {
        Predicate userIdPredicate = qRoom.members.get(0).userId.in(userid, friendid);
        Predicate friendIdPredicate = qRoom.members.get(1).userId.in(userid, friendid);
        Predicate sizePredicate = qRoom.members.size().eq(2);
        Predicate predicate = ((BooleanExpression) userIdPredicate).and(
                friendIdPredicate).and(sizePredicate);
        return roomRepository.findOne(predicate)
                .orElseThrow(() -> new GlobalException(GlobalError.BAD_REQUEST, "존재하지 않는 1:1 채팅방입니다."));
    }

    public Room saveMessageId(String roomId, ChatMessage chatMessage) {
        Room room = this.find(roomId);
        if(chatMessage.getType() == MessageType.AWAKE.ordinal()) {
            room.getMembers().stream()
                    .filter(roomMember -> !roomMember.getIsJoining())
                    .map(roomMember -> RoomMember.builder()
                            .userId(roomMember.getUserId())
                            .isJoining(true)
                            .joinedAt(roomMember.getJoinedAt())
                            .leftAt(roomMember.getLeftAt())
                            .build());
        }
        List<ObjectId> messageIds = room.getMessageIds();
        messageIds.add(chatMessage.getId());
        return this.save(room);
    }

    public void invite(String roomId, List<Long> userIds) {
        Room room = this.find(roomId);
        // userIds로 User 서버에서 정보 조회
        List<RoomMember> members = room.getMembers();
        List<RoomMember> invitees = userIds.stream()
                .map(id -> RoomMember.builder()
                        .userId(id)
                        .joinedAt(LocalDateTime.now())
                        .build())
                // 중복되게 들어가면 안댐
                .filter(invitee -> !members.contains(invitee))
                .collect(Collectors.toList());
        members.addAll(invitees);
        this.save(room);
    }

    public void leave(String roomId, Long userId) {
        Room room = this.find(roomId);
        RoomMember leaver = this.findMember(room, userId);
        List<RoomMember> members = room.getMembers();
        members.remove(leaver);
        if (room.getType() == 0) {
            members.add(RoomMember.builder()
                    .userId(leaver.getUserId())
                    .isJoining(false)
                    .joinedAt(leaver.getJoinedAt())
                    .leftAt(LocalDateTime.now()) // 어차피 ChannelInterceptor preSend에서 처리해주지 않을까?
                    .build());
        }
        if (members.size() == 0) this.delete(room); // 1:1은 수정만 하기 때문에 단톡방에만 적용
        else this.save(room);
    }

    public void delete(Room room) {
        roomRepository.delete(room);
    }
}
