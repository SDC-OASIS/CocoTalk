package com.cocotalk.chat.service;

import com.cocotalk.chat.domain.entity.room.QRoom;
import com.cocotalk.chat.domain.entity.room.Room;
import com.cocotalk.chat.domain.entity.room.RoomMember;
import com.cocotalk.chat.domain.vo.*;
import com.cocotalk.chat.dto.request.ChatMessageRequest;
import com.cocotalk.chat.dto.request.InviteMessageRequest;
import com.cocotalk.chat.dto.request.RoomRequest;
import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import com.cocotalk.chat.repository.RoomRepository;
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
    private final MessageBundleService messageBundleService;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    QRoom qRoom = QRoom.room;

    public static final Room emptyRoom = new Room();
    public static final List<ObjectId> emptyObjectIdList = new ArrayList<>();
    public static final CustomException INVALID_ROOM_ID =
            new CustomException(CustomError.BAD_REQUEST, "해당 roomId를 갖는 채팅방이 존재하지 않습니다.");
    public static final CustomException INVALID_ROOM_MEMBER =
            new CustomException(CustomError.BAD_REQUEST, "해당 채팅방에 속해 있지 않은 유저입니다.");
    public static final CustomException WRONG_MEMBER_SIZE =
            new CustomException(CustomError.BAD_REQUEST, "채팅방 멤버는 둘 이상이어야 합니다.");

    public RoomVo create(RoomRequest request) { // C
        LocalDateTime now = LocalDateTime.now();

        List<Long> memberIds = request.getMemberIds();

        if (memberIds.size() >= 2) {
            List<RoomMember> roomMembers = request.getMemberIds().stream()
                    .map(userId -> RoomMember.builder()
                            .userId(userId)
                            .joinedAt(now)
                            .joining(true)
                            .leftAt(now)
                            .build())
                    .collect(Collectors.toList());

            Room createdRoom = roomRepository.save(Room.builder()
                    .name(request.getName())
                    .img(request.getImg())
                    .type(request.getType())
                    .members(roomMembers)
                    .messageBundleIds(emptyObjectIdList)
                    .noticeIds(emptyObjectIdList)
                    .build());

            return roomMapper.toVo(createdRoom);
        } else {
            throw WRONG_MEMBER_SIZE;
        }
    }

    public RoomVo save(RoomVo roomVo){
        Room room = roomRepository.save(roomMapper.toEntity(roomVo));
        return roomMapper.toVo(room);
    }

    public ChatMessageVo saveChatMessage(ObjectId roomId, ChatMessageRequest request) {
        RoomVo roomVo = this.findById(roomId); // 룸 찾고
        int beforeSize = roomVo.getMessageBundleIds().size();
        MessageSaveVo messageSaveVo = messageBundleService.findRecentMessageBundle(roomVo);
        roomVo = messageSaveVo.getRoomVo();
        int afterSize = roomVo.getMessageBundleIds().size();

        if(beforeSize != afterSize) this.save(roomVo);

        return chatMessageService.createChatMessage(request, messageSaveVo.getMessageBundleVo());
    }

    public InviteMessageVo saveInviteMessage(ObjectId roomId, InviteMessageRequest request) {
        RoomVo roomVo = this.findById(roomId);
        
        MessageSaveVo messageSaveVo = messageBundleService.findRecentMessageBundle(roomVo);
        roomVo = messageSaveVo.getRoomVo();

        List<RoomMember> members = roomVo.getMembers();

        LocalDateTime now = LocalDateTime.now();

        List<RoomMember> invitees = request.getInviteeIds().stream()
                .map(id -> RoomMember.builder()
                        .userId(id)
                        .joining(true)
                        .joinedAt(now)
                        .leftAt(now)
                        .build())
                .filter(invitee -> !members.contains(invitee)) // 중복 제거
                .collect(Collectors.toList());
        members.addAll(invitees);
        
        this.save(roomVo); // member 업데이트 때문에 필수

        return chatMessageService.createInviteMessage(request, messageSaveVo.getMessageBundleVo());
    }

    public ChatMessageVo saveLeaveMessage(ObjectId roomId, ChatMessageRequest request) {
        RoomVo roomVo = this.findById(roomId);

        MessageSaveVo messageSaveVo = messageBundleService.findRecentMessageBundle(roomVo);
        ChatMessageVo chatMessageVo = chatMessageService.createChatMessage(request, messageSaveVo.getMessageBundleVo());
        roomVo = messageSaveVo.getRoomVo();

        RoomMember leaver = this.findMember(roomVo, chatMessageVo.getUserId());
        List<RoomMember> members = roomVo.getMembers();
        members.remove(leaver);
        if (members.size() == 0) this.delete(roomId);
        else this.save(roomVo);

        return chatMessageVo;
    }

    public Room find(ObjectId roomId) { // R
        return roomRepository.findById(roomId).orElseThrow(() -> INVALID_ROOM_ID);
    } // Service 내부에서만 사용

    public RoomVo findById(ObjectId roomId) {
        Room room = this.find(roomId);
        return roomMapper.toVo(room);
    }

    public List<RoomVo> findAll(UserVo userVo) {
        return roomRepository.findByMembersUserIdAndMembersJoiningIsTrue(userVo.getId()).stream()
                .map(roomMapper::toVo)
                .collect(Collectors.toList());
    }

    public List<RoomListVo> findRoomList(UserVo userVo) { // sort 타입 패러미터 추가 예정
        Comparator<RoomListVo> comparator = (o1, o2) ->
                o2.getLastChatMessageVo().getSentAt().compareTo(o1.getLastChatMessageVo().getSentAt());

        return roomRepository.findByMembersUserIdAndMembersJoiningIsTrue(userVo.getId()).stream() // parallel?
                .map(roomMapper::toVo)
                .map(roomVo -> {
                    RoomMember me = this.findMember(roomVo, userVo.getId());

                    List<ObjectId> messageBundleIds = roomVo.getMessageBundleIds();

                    long amountUnread = 0;
                    int mbIdx = messageBundleIds.size() - 1; // messageBundleId는 room 생성시 자동 생성되기 때문에 무조건 하나는 있음 
                    MessageBundleVo messageBundle = messageBundleService.find(messageBundleIds.get(mbIdx));
                    List<ObjectId> messageIds = messageBundle.getMessageIds(); // slicing 필요?

                    ChatMessageVo lastChatMessageVo = chatMessageService.find(messageIds.get(messageIds.size() - 1));

                    // Pagination을 제한적으로 사용하기 때문에 (전부 읽었을 수도 있고, 일정 갯수 넘어가면 탈출) 따로 limit 쿼리를 쓸 필요는 없어 보인다.
                    while(amountUnread < 10 && mbIdx >= 0) {
                        // 현재 메시지 번들에서 읽지 않은 메시지 수 계산
                        long partUnread = messageBundle.getMessageIds().stream() // parallel?
                                .map(chatMessageService::find)
                                .filter(chatMessage -> chatMessage.getSentAt().isAfter(me.getLeftAt())) // 인자보다 미래시간일때 true 반환
                                .count();
                        if(partUnread == 0) {
                            break; // 더 이상 안 읽은 메시지가 없음
                        }
                        else {
                            amountUnread += partUnread;
                            if(amountUnread >= 10) amountUnread = 10;
                            messageBundle = messageBundleService.find(messageBundleIds.get(mbIdx--)); // 이전 메시지 번들로 갱신
                        }
                    }

                    return RoomListVo.builder()
                            .id(roomVo.getId())
                            .name(roomVo.getName())
                            .img(roomVo.getImg())
                            .type(roomVo.getType())
                            .lastChatMessageVo(lastChatMessageVo)
                            .unreadNumber(amountUnread)
                            .build();
                })
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public RoomMember findMember(RoomVo roomVo, Long userId) {
        return roomVo.getMembers().stream()
                .filter(roomMember -> userId.equals(roomMember.getUserId()))
                .findFirst()
                .orElseThrow(() -> INVALID_ROOM_MEMBER);
    } // Service 내부에서만 사용

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

    public RoomVo modify(ObjectId roomId, RoomRequest request) { // U
        Room oldRoom = this.find(roomId);
        Room newRoom = roomMapper.toEntity(request);
        newRoom.setId(oldRoom.getId());
        return roomMapper.toVo(roomRepository.save(newRoom));
    }

    public void saveLeftAt(ObjectId roomId, Long userId) {
        RoomVo roomVo = this.findById(roomId);
        RoomMember me = this.findMember(roomVo, userId);
        List<RoomMember> members = roomVo.getMembers();

        members.remove(me);
        me.setLeftAt(LocalDateTime.now());
        members.add(me);
        this.save(roomVo);
    }

    public String delete(ObjectId roomId) {
        Room room = this.find(roomId);
        roomRepository.delete(room);
        return String.format("roomId = \"%s\" 가 정상적으로 삭제되었습니다.", roomId);
    }
}
