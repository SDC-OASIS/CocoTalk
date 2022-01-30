package com.cocotalk.chat.service;

import com.cocotalk.chat.domain.entity.message.ChatMessage;
import com.cocotalk.chat.domain.entity.message.MessageType;
import com.cocotalk.chat.domain.entity.room.QRoom;
import com.cocotalk.chat.domain.entity.room.Room;
import com.cocotalk.chat.domain.entity.room.RoomMember;
import com.cocotalk.chat.domain.vo.ChatMessageVo;
import com.cocotalk.chat.domain.vo.RoomListVo;
import com.cocotalk.chat.domain.vo.RoomVo;
import com.cocotalk.chat.domain.vo.UserVo;
import com.cocotalk.chat.dto.request.RoomRequest;
import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import com.cocotalk.chat.repository.RoomRepository;
import com.cocotalk.chat.utils.mapper.ChatMessageMapper;
import com.cocotalk.chat.utils.mapper.RoomMapper;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final ChatMessageService chatMessageService;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final ChatMessageMapper chatMessageMapper;

    QRoom qRoom = QRoom.room;

    public static final Room emptyRoom = new Room();
    public static final List<ObjectId> emptyObjectIdList = new ArrayList<>();
    public static final CustomException INVALID_ROOMID =
            new CustomException(CustomError.BAD_REQUEST, "해당 roomId를 갖는 채팅방이 존재하지 않습니다.");
    public static final CustomException INVALID_ROOMMEMBER =
            new CustomException(CustomError.BAD_REQUEST, "해당 채팅방에 속해 있지 않은 유저입니다.");

    public RoomVo create(RoomRequest request) { // C
        LocalDateTime now = LocalDateTime.now();
        List<RoomMember> roomMembers = request.getMemberIds().parallelStream()
                .map(userId -> RoomMember.builder()
                            .userId(userId)
                            .joinedAt(now)
                            .build())
                .collect(Collectors.toList());

        Room createdRoom = Room.builder()
                .name(request.getName())
                .img(request.getImg())
                .type(request.getType())
                .members(roomMembers)
                .messageIds(emptyObjectIdList)
                .noticeIds(emptyObjectIdList)
                .build();

        Room room = roomRepository.save(createdRoom);
        return roomMapper.toVo(room);
    }

    public Room find(String id) { // R
        return roomRepository.findById(id).orElseThrow(() -> INVALID_ROOMID);
    } // Service 내부에서만 사용

    public RoomVo findById(String id) {
        Room room = this.find(id);
        return roomMapper.toVo(room);
    }

    public List<RoomVo> findAll(UserVo userVo) {
        return roomRepository.findByMembersUserId(userVo.getId()).stream()
                .map(roomMapper::toVo)
                .collect(Collectors.toList());
    }

    public List<RoomListVo> findRoomList(UserVo userVo) { // sort 타입 패러미터 추가 예정
        Comparator<RoomListVo> comparator = (o1, o2) ->
                o2.getLastChatMessageVo().getSentAt().compareTo(o1.getLastChatMessageVo().getSentAt());

        return roomRepository.findByMembersUserId(userVo.getId()).stream() // 나중에 소팅하니까 parallel로 바꿔도 될 듯?
                .map(room -> {
                    RoomMember me = this.findMember(room, userVo.getId());

                    List<ObjectId> messageIds = room.getMessageIds();
                    int size = messageIds.size();
                    int fromIndex;
                    if(size < 300) fromIndex = 0;
                    else fromIndex = size - 300;

                    List<ObjectId> recentMessageIds = messageIds.subList(fromIndex, size);

                    List<ChatMessage> recentMessages = recentMessageIds.stream()
                            .map(chatMessageService::find)
                            .collect(Collectors.toList());

                    Long unreadNumber = recentMessages.parallelStream()
                            .filter(chatMessage -> chatMessage.getSentAt().isAfter(me.getLeftAt()))// 인자보다 미래시간일때 true 반환
                            .count();

                    ChatMessageVo lastChatMessageVo = chatMessageMapper.toVo(recentMessages.get(recentMessages.size() - 1));

                    return RoomListVo.builder()
                            .id(room.getId())
                            .name(room.getName())
                            .img(room.getImg())
                            .type(room.getType())
                            .lastChatMessageVo(lastChatMessageVo)
                            .unreadNumber(unreadNumber)
                            .build();
                })
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public RoomMember findMember(Room room, Long userId) {
        return room.getMembers().stream()
                .filter(roomMember -> userId.equals(roomMember.getUserId()))
                .findFirst()
                .orElseThrow(() -> INVALID_ROOMMEMBER);
    }

    public RoomVo findPrivate(UserVo userVo, Long friendId) {
        Long userId = userVo.getId();
        Predicate userIdPredicate = qRoom.members.get(0).userId.in(userId, friendId);
        Predicate friendIdPredicate = qRoom.members.get(1).userId.in(userId, friendId);
        Predicate sizePredicate = qRoom.members.size().eq(2);
        Predicate predicate = ((BooleanExpression) userIdPredicate).and(
                friendIdPredicate).and(sizePredicate);
        Room room = roomRepository.findOne(predicate).orElse(emptyRoom);
        return roomMapper.toVo(room);
    }

    public RoomVo modify(String id, RoomRequest request) { // U
        Room oldRoom = this.find(id);
        Room newRoom = roomMapper.toEntity(request);
        newRoom.setId(oldRoom.getId());
        return roomMapper.toVo(roomRepository.save(newRoom));
    }

    public void saveMessageId(String roomId, ChatMessage chatMessage) {
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
        roomRepository.save(room);
    }

    public void saveLeftAt(String roomId, Long userId) {
        Room room = this.find(roomId);
        RoomMember me = this.findMember(room, userId);
        List<RoomMember> members = room.getMembers();
        members.remove(me);
        me.setLeftAt(LocalDateTime.now());
        members.add(me);
        roomRepository.save(room);
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
        roomRepository.save(room);
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
        if (members.size() == 0) this.delete(roomId); // 1:1은 수정만 하기 때문에 단톡방에만 적용
        else roomRepository.save(room);
    }

    public void away(String roomId, Long userId) {
        Room room = this.find(roomId);
        RoomMember awayMember = this.findMember(room, userId);
        List<RoomMember> members = room.getMembers();
        members.remove(awayMember);
        members.add(RoomMember.builder()
                .userId(awayMember.getUserId())
                .isJoining(true)
                .joinedAt(awayMember.getJoinedAt())
                .leftAt(LocalDateTime.now())
                .build());
        roomRepository.save(room);
    }

    public String delete(String id) {
        Room room = this.find(id);
        roomRepository.delete(room);
        return String.format("roomId = \"%s\" 가 정상적으로 삭제되었습니다.", id);
    }
}
