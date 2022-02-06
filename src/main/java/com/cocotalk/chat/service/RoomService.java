package com.cocotalk.chat.service;

import com.cocotalk.chat.domain.entity.room.QRoom;
import com.cocotalk.chat.domain.entity.room.Room;
import com.cocotalk.chat.domain.entity.room.RoomMember;
import com.cocotalk.chat.domain.entity.room.RoomType;
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
import org.springframework.beans.factory.annotation.Value;
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

    @Value(value = "${cocotalk.message-bundle-limit}")
    private int messageBundleLimit;

    QRoom qRoom = QRoom.room;

    public static final Room emptyRoom = new Room();
    public static final ChatMessageVo emptyChatMessageVo = ChatMessageVo.builder()
            .content("채팅방에 메시지가 없습니다.")
            .build();
    public static final List<ObjectId> emptyObjectIdList = new ArrayList<>();


    public static final CustomException INVALID_ROOM_ID =
            new CustomException(CustomError.BAD_REQUEST, "해당 roomId를 갖는 채팅방이 존재하지 않습니다.");
    public static final CustomException INVALID_ROOM_MEMBER =
            new CustomException(CustomError.BAD_REQUEST, "해당 채팅방에 속해 있지 않은 유저입니다.");
    public static final CustomException WRONG_MEMBER_SIZE =
            new CustomException(CustomError.BAD_REQUEST, "채팅방 멤버는 둘 이상이어야 합니다.");
    public static final CustomException TARGET_YOURSELF =
            new CustomException(CustomError.BAD_REQUEST, "자기 자신을 대상으로 할 수 없습니다.");
    public static final CustomException INVALID_MESSAGE_TYPE =
            new CustomException(CustomError.BAD_REQUEST, "해당 채팅방에서 사용할 수 없는 메시지 타입입니다.");
    public static final CustomException DUPLICATED_ROOM_MEMBER =
            new CustomException(CustomError.BAD_REQUEST, "현재 채팅방에 참가 중인 유저를 초대할 수 없습니다.");
    public static final CustomException EXCEEDED_ROOM_MEMBER =
            new CustomException(CustomError.BAD_REQUEST, "초대한 유저보다 추가하려는 유저 수가 많습니다.");

    public RoomVo create(RoomRequest request) { // C
        LocalDateTime now = LocalDateTime.now();

        List<Long> memberIds = request.getMemberIds().stream().distinct().collect(Collectors.toList()); // 중복 제거

        if (memberIds.size() >= 2) {
            List<RoomMember> roomMembers = request.getMemberIds().stream()
                    .map(userId -> RoomMember.builder()
                            .userId(userId)
                            .joinedAt(now)
                            .joining(true)
                            .awayAt(now)
                            .leftAt(now)
                            .build())
                    .collect(Collectors.toList());

            Room createdRoom = roomRepository.save(Room.builder()
                    .roomName(request.getRoomName())
                    .img(request.getImg())
                    .type(request.getType())
                    .members(roomMembers)
                    .messageBundleIds(emptyObjectIdList)
                    .noticeIds(emptyObjectIdList)
                    .build());

            MessageBundleVo messageBundleVo = messageBundleService.create(createdRoom.getId());
            createdRoom.getMessageBundleIds().add(messageBundleVo.getId());
            return roomMapper.toVo(roomRepository.save(createdRoom));
        } else {
            throw WRONG_MEMBER_SIZE;
        }
    }

    public RoomVo save(RoomVo roomVo){
        Room room = roomRepository.save(roomMapper.toEntity(roomVo));
        return roomMapper.toVo(room);
    }

    public MessageVo<ChatMessageVo> saveChatMessage(ObjectId roomId, ChatMessageRequest request) {
        MessageVo<ChatMessageVo> messageVo = chatMessageService.createChatMessage(request); // (1) 메시지 저장
        BundleInfoVo bundleInfoVo = messageVo.getBundleInfo(); // (6) 이 메시지가 저장된 번들 Id와 다음 메시지가 저장할 번들 Id가 들어있는 Vo
        if(!bundleInfoVo.getCurrentMessageBundleId().equals(bundleInfoVo.getNextMessageBundleId())) {
            RoomVo roomVo = this.findById(roomId);
            roomVo.getMessageBundleIds().add(bundleInfoVo.getNextMessageBundleId());
            this.save(roomVo);
        }
        return messageVo;
    }

    public MessageWithRoomVo<InviteMessageVo> saveInviteMessage(ObjectId roomId, InviteMessageRequest request) {
        MessageVo<InviteMessageVo> messageVo = chatMessageService.createInviteMessage(request);
        BundleInfoVo bundleInfoVo = messageVo.getBundleInfo();

        RoomVo roomVo = this.findById(roomId);

        if(!bundleInfoVo.getCurrentMessageBundleId().equals(bundleInfoVo.getNextMessageBundleId()))
            roomVo.getMessageBundleIds().add(bundleInfoVo.getNextMessageBundleId());

        List<RoomMember> members = roomVo.getMembers();

        LocalDateTime now = LocalDateTime.now();

        List<RoomMember> invitees = messageVo.getMessage().getInviteeIds().stream()
                .distinct() // 중복 입력 제거
                .map(id -> RoomMember.builder()
                        .userId(id)
                        .joining(true)
                        .joinedAt(now)
                        .awayAt(now)
                        .leftAt(now)
                        .build())
                .filter(invitee -> !members.contains(invitee)) // 기존 멤버와 중복 제거
                .collect(Collectors.toList());
        
        int estimateSize = members.size() + request.getInviteeIds().size();
        int amountSize = members.size() + invitees.size();
        if(estimateSize != amountSize) {
            if(estimateSize > amountSize) {
                throw DUPLICATED_ROOM_MEMBER;
            } else  {
                throw EXCEEDED_ROOM_MEMBER;
            }
        }
        
        members.addAll(invitees);

        roomVo = this.save(roomVo); // member 업데이트 때문에 필수

        return new MessageWithRoomVo<>(messageVo, roomVo);
    }

    public MessageWithRoomVo<ChatMessageVo> saveLeaveMessage(ObjectId roomId, ChatMessageRequest request) {
        boolean saveRoom = false;

        MessageVo<ChatMessageVo> messageVo = chatMessageService.createChatMessage(request);
        ChatMessageVo leaveMessageVo = messageVo.getMessage();
        BundleInfoVo bundleInfoVo = messageVo.getBundleInfo();

        RoomVo roomVo = this.findById(roomId);

        if(!bundleInfoVo.getCurrentMessageBundleId().equals(bundleInfoVo.getNextMessageBundleId())) {
            roomVo.getMessageBundleIds().add(bundleInfoVo.getNextMessageBundleId());
            saveRoom = true;
        }

        RoomMember leaver = this.findMember(roomVo, leaveMessageVo.getUserId());

        List<RoomMember> members = roomVo.getMembers();
        members.remove(leaver);

        if(roomVo.getType() == RoomType.PRIVATE.ordinal()) {
            leaver.setJoining(false);
            members.add(leaver);
            saveRoom = true;
        } else {
            if (members.size() == 0) this.deleteById(roomId);
            else saveRoom = true;
        }

        if(saveRoom) this.save(roomVo);

        return new MessageWithRoomVo<>(messageVo, roomVo);
    }

    public Room find(ObjectId roomId) { // R
        return roomRepository.findById(roomId).orElseThrow(() -> INVALID_ROOM_ID);
    } // Service 내부에서만 사용

    public RoomVo findById(ObjectId roomId) {
        Room room = this.find(roomId);
        return roomMapper.toVo(room);
    }

    public List<RoomVo> findAllJoining(UserVo userVo) {
        return roomRepository.findJoiningRoom(userVo.getId()).stream()
                .map(roomMapper::toVo)
                .collect(Collectors.toList());
    }



    public List<RoomListVo> findRoomList(UserVo userVo) { // sort 타입 패러미터 추가 예정
        Comparator<RoomListVo> comparator = (o1, o2) ->
                o2.getRecentChatMessage().getSentAt().compareTo(o1.getRecentChatMessage().getSentAt());

        // (1) 내가 참가중인 모든 방 조회
        return this.findAllJoining(userVo).stream()
                .map(roomVo -> {
                    RoomMember me = this.findMember(roomVo, userVo.getId()); // (2) 방 내부 내 정보 조회

                    List<ObjectId> messageBundleIds = roomVo.getMessageBundleIds(); // (3) 방 내부 메시지 번들 조회

                    long amountUnread = 0;
                    int mbIdx = messageBundleIds.size() - 1; // (4) messageBundleId는 room 생성시 자동 생성되기 때문에 무조건 하나는 있음
                    MessageBundleVo messageBundleVo = messageBundleService.find(messageBundleIds.get(mbIdx)); // (5) 가장 최신 메시지 번들
                    int recentMessageBundleCount = messageBundleVo.getCount();

                    List<ObjectId> messageIds = messageBundleVo.getMessageIds(); // slicing 필요?

                    ChatMessageVo recentChatMessageVo;
                    if(messageIds.size() > 0) recentChatMessageVo = chatMessageService.find(messageIds.get(messageIds.size() - 1));
                    else recentChatMessageVo = emptyChatMessageVo;

                    // Pagination을 제한적으로 사용하기 때문에 (전부 읽었을 수도 있고, 일정 갯수 넘어가면 탈출) 따로 limit 쿼리를 쓸 필요는 없어 보인다.
                    while(amountUnread < messageBundleLimit && mbIdx >= 0) {
                        // 현재 메시지 번들에서 읽지 않은 메시지 수 계산
                        long partUnread = messageBundleVo.getMessageIds().stream() // parallel?
                                .map(chatMessageService::find)
                                .filter(chatMessage -> chatMessage.getSentAt().isAfter(me.getAwayAt())) // 인자보다 미래시간일때 true 반환
                                .count();
                        if(partUnread == 0) {
                            break; // 더 이상 안 읽은 메시지가 없음
                        }
                        else {
                            amountUnread += partUnread;
                            if(amountUnread >= messageBundleLimit) amountUnread = messageBundleLimit;
                            messageBundleVo = messageBundleService.find(messageBundleIds.get(mbIdx--)); // 이전 메시지 번들로 갱신
                        }
                    }

                    return RoomListVo.builder()
                            .room(roomVo)
                            .recentChatMessage(recentChatMessageVo)
                            .recentMessageBundleCount(recentMessageBundleCount)
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

    public RoomVo findPrivate(UserVo userVo, Long userId) {
        if(userVo.getId().equals(userId)) throw TARGET_YOURSELF;
        Predicate userIdPredicate = qRoom.members.get(0).userId.in(userVo.getId(), userId);
        Predicate friendIdPredicate = qRoom.members.get(1).userId.in(userVo.getId(), userId);
        BooleanExpression roomTypePredicate = qRoom.type.eq(RoomType.PRIVATE.ordinal());
        Predicate predicate = roomTypePredicate.and(friendIdPredicate).and(userIdPredicate);
        Room room = roomRepository.findOne(predicate).orElse(emptyRoom);
        return roomMapper.toVo(room);
    }

    public RoomVo modify(ObjectId roomId, RoomRequest request) { // U
        Room oldRoom = this.find(roomId);
        Room newRoom = roomMapper.toEntity(request);
        newRoom.setId(oldRoom.getId());
        return roomMapper.toVo(roomRepository.save(newRoom));
    }

    public void saveAwayAt(ObjectId roomId, Long userId) {
        RoomVo roomVo = this.findById(roomId);
        RoomMember me = this.findMember(roomVo, userId);
        List<RoomMember> members = roomVo.getMembers();

        members.remove(me);
        me.setAwayAt(LocalDateTime.now());
        members.add(me);
        this.save(roomVo);
    }

    public void saveLeftAt(ObjectId roomId, Long userId) {
        RoomVo roomVo = this.findById(roomId);
        RoomMember me = this.findMember(roomVo, userId);
        List<RoomMember> members = roomVo.getMembers();

        members.remove(me);

        LocalDateTime now = LocalDateTime.now();

        if(roomVo.getType() == RoomType.PRIVATE.ordinal()) {
            me.setJoining(false);
            me.setAwayAt(now);
            me.setLeftAt(now);
            members.add(me);
            this.save(roomVo);
        } else {
            if(members.size() == 0) delete(roomVo);
            else this.save(roomVo);
        }
    }

    public String delete(RoomVo roomVo) {
        roomRepository.delete(roomMapper.toEntity(roomVo));
        return String.format("roomId = \"%s\" 가 정상적으로 삭제되었습니다.", roomVo.getId());
    }

    public String deleteById(ObjectId roomId) {
        Room room = this.find(roomId);
        roomRepository.delete(room);
        return String.format("roomId = \"%s\" 가 정상적으로 삭제되었습니다.", roomId);
    }
}
