package com.cocotalk.chat.service;

import com.cocotalk.chat.domain.entity.room.QRoom;
import com.cocotalk.chat.domain.entity.room.Room;
import com.cocotalk.chat.domain.entity.room.RoomMember;
import com.cocotalk.chat.domain.entity.room.RoomType;
import com.cocotalk.chat.domain.vo.*;
import com.cocotalk.chat.dto.request.*;
import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import com.cocotalk.chat.repository.RoomRepository;
import com.cocotalk.chat.utils.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {
    private final ChatMessageService chatMessageService;
    private final MessageBundleService messageBundleService;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Value(value = "${cocotalk.message-bundle-limit}")
    private int messageBundleLimit;

    @Value(value = "${cocotalk.message-paging-size}")
    private int messagePagingSize;

    QRoom qRoom = QRoom.room;

    public static final Room emptyRoom = new Room();
    public static final ChatMessageVo emptyChatMessageVo = ChatMessageVo.builder()
            .content("채팅방에 메시지가 없습니다.")
            .sentAt(LocalDateTime.now()) // 임시 방편
            .build();

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

        // (1) 채팅방 멤버 중복 제거
        List<RoomMemberRequest> members = request.getMembers().stream().distinct().collect(Collectors.toList());

        // (2) 채팅방 생성시 멤버는 2명 이상이어야 함
        if (members.size() >= 2) {
            List<RoomMember> roomMembers = request.getMembers().stream()
                    .map(member -> RoomMember.builder()
                            .userId(member.getUserId())
                            .username(member.getUsername())
                            .profile(member.getProfile())
                            .joinedAt(now)
                            .enteredAt(now)
                            .joining(true)
                            .awayAt(now)
                            .leftAt(now)
                            .build())
                    .collect(Collectors.toList());

            Room createdRoom = roomRepository.save(Room.builder()
                    .roomname(request.getRoomname())
                    .img(request.getImg())
                    .type(request.getType())
                    .members(roomMembers)
                    .messageBundleIds(new ArrayList<>())
                    .noticeIds(new ArrayList<>())
                    .build());

            // (3) 방 생성시 메시지 번들도 함께 생성
            MessageBundleVo messageBundleVo = messageBundleService.create(createdRoom.getId());
            createdRoom.getMessageBundleIds().add(messageBundleVo.getId());
            return roomMapper.toVo(roomRepository.save(createdRoom));
        } else {
            throw WRONG_MEMBER_SIZE;
        }
    }

    public RoomVo save(RoomVo roomVo) {
        Room room = roomRepository.save(roomMapper.toEntity(roomVo));
        return roomMapper.toVo(room);
    }

    public MessageVo<ChatMessageVo> saveChatMessage(ObjectId roomId, ChatMessageRequest request) {
        log.info("chatMessageRequest : {}", request.toString());
        MessageVo<ChatMessageVo> messageVo = chatMessageService.createChatMessage(request); // (1) 메시지 저장
        // (2) 윗라인에서 저장된 메시지의 Id가 들어있는 메시지 번들 Id와 다음 메시지가 저장될 번들 Id가 들어있는 Vo
        BundleInfoVo bundleInfoVo = messageVo.getBundleInfo();
        // (3) 새로운 메시지 번들이 생성되었을 때
        if(!bundleInfoVo.getCurrentMessageBundleId().equals(bundleInfoVo.getNextMessageBundleId())) {
            RoomVo roomVo = this.findById(roomId);
            // (4) 채팅방에 메시지 번들 추가 후 저장
            roomVo.getMessageBundleIds().add(bundleInfoVo.getNextMessageBundleId());
            this.save(roomVo);
        }
        return messageVo;
    }

    public MessageWithRoomVo<InviteMessageVo> saveInviteMessage(ObjectId roomId, InviteMessageRequest request) {
        log.info("inviteMessageRequest : {}", request.toString());
        MessageVo<InviteMessageVo> messageVo = chatMessageService.createInviteMessage(request); // (1) 초대 메시지 저장
        BundleInfoVo bundleInfoVo = messageVo.getBundleInfo();

        RoomVo roomVo = this.findById(roomId);

        if(!bundleInfoVo.getCurrentMessageBundleId().equals(bundleInfoVo.getNextMessageBundleId()))
            roomVo.getMessageBundleIds().add(bundleInfoVo.getNextMessageBundleId());

        List<RoomMember> members = roomVo.getMembers();

        LocalDateTime now = LocalDateTime.now();

        // (2) 초대 요청 모델로 채팅방 멤버 생성
        List<RoomMember> invitees = messageVo.getMessage().getInvitees().stream()
                .distinct() // (3) 중복 입력 제거
                .map(member -> RoomMember.builder()
                        .userId(member.getUserId())
                        .username(member.getUsername())
                        .profile(member.getProfile())
                        .joining(true)
                        .joinedAt(now)
                        .enteredAt(now)
                        .awayAt(now)
                        .leftAt(now)
                        .build())
                .filter(invitee -> !members.contains(invitee)) // (4) 기존 멤버와 중복 제거
                .collect(Collectors.toList());

        // (5) 초대 전 후 멤버 수 확인 -> 불일치시 Exception
        int estimateSize = members.size() + request.getInvitees().size();
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
        log.info("leaveMessageRequest : {}", request.toString());
        boolean saveRoom = false;

        MessageVo<ChatMessageVo> messageVo = chatMessageService.createChatMessage(request); // (1) 나가기 메시지 저장
        ChatMessageVo leaveMessageVo = messageVo.getMessage();
        BundleInfoVo bundleInfoVo = messageVo.getBundleInfo();

        RoomVo roomVo = this.findById(roomId);

        if(!bundleInfoVo.getCurrentMessageBundleId().equals(bundleInfoVo.getNextMessageBundleId())) {
            roomVo.getMessageBundleIds().add(bundleInfoVo.getNextMessageBundleId());
            saveRoom = true;
        }

        RoomMember leaver = this.findMember(roomVo, leaveMessageVo.getUserId()); // (2) 나가는 채팅방 멤버 찾기

        List<RoomMember> members = roomVo.getMembers();
        members.remove(leaver);

        if(roomVo.getType() == RoomType.PRIVATE.ordinal()) { // 1:1 채팅방에서는 members에서 삭제하는 것이 아니라
            leaver.setJoining(false); // joining flag를 바꿔줘서 리스트에 뜨지 않게만 한다.
            members.add(leaver);
            saveRoom = true;
        } else { // 단체 채팅방에서는 members에서 삭제한다.
            if (members.size() == 0) this.deleteById(roomId); // member 수가 0이된 단톡방은 삭제한다.
            else saveRoom = true;
        }

        if(saveRoom) this.save(roomVo);

        return new MessageWithRoomVo<>(messageVo, roomVo);
    }

    public MessageWithRoomVo<ChatMessageVo> saveAwakeMessage(ObjectId roomId, ChatMessageRequest request) {
        LocalDateTime now = LocalDateTime.now();

        log.info("awakeMessageRequest : {}", request.toString());
        MessageVo<ChatMessageVo> messageVo = chatMessageService.createChatMessage(request);
        BundleInfoVo bundleInfoVo = messageVo.getBundleInfo();
        Room room = this.find(roomId);
        // (1) Awake 메시지는 1:1 채팅방에서 나간 사람이 있을 경우 joining flag를 true로 바꿔줍니다.
        room.setMembers(room.getMembers().stream()
                .map(roomMember -> {
                    roomMember.setJoinedAt(now);
                    roomMember.setEnteredAt(now);
                    roomMember.setJoining(true);
                    return roomMember;
                })
                .collect(Collectors.toList()));
        if(!bundleInfoVo.getCurrentMessageBundleId().equals(bundleInfoVo.getNextMessageBundleId())) {
            room.getMessageBundleIds().add(bundleInfoVo.getNextMessageBundleId());
        }
        RoomVo roomVo = roomMapper.toVo(roomRepository.save(room));
        return new MessageWithRoomVo<>(messageVo, roomVo);
    }

    public Room find(ObjectId roomId) { // R
        return roomRepository.findById(roomId).orElseThrow(() -> INVALID_ROOM_ID);
    } // Service 내부에서만 사용

    public RoomVo findById(ObjectId roomId) {
        Room room = this.find(roomId);
        return roomMapper.toVo(room);
    }

    public RoomWithMessageListVo<ChatMessageVo> findRoomAndMessage(ObjectId roomId,
                                     @RequestParam int count) { // room 정보와 첫 메시지 페이지를 함께 찾아줍니다
        RoomVo roomVo = roomMapper.toVo(this.find(roomId));
        List<ObjectId> messageBundleIds = roomVo.getMessageBundleIds();
        List<ChatMessageVo> chatMessageVos = chatMessageService.findMessagePage(
                roomId,
                messageBundleIds.get(messageBundleIds.size() - 1),
                count,
                messagePagingSize);
        return new RoomWithMessageListVo<>(roomVo, chatMessageVos);
    }

    public List<RoomVo> findAllJoining(UserVo userVo) { // 참가중인 모든 방 정보를 조회합니다.
        return roomRepository.findJoiningRoom(userVo.getId()).stream()
                .map(roomMapper::toVo)
                .collect(Collectors.toList());
    }

    public List<RoomListVo> findRoomList(UserVo userVo) { // sort 타입 패러미터 추가 예정
        Comparator<RoomListVo> comparator = (o1, o2) ->
                o2.getRecentChatMessage().getSentAt().compareTo(o1.getRecentChatMessage().getSentAt());

        // (1) 내가 참가중인 모든 방 리스트, 수신한 최근 메시지, 읽지 않은 메시지 수 조회
        return this.findAllJoining(userVo).stream()
                .map(roomVo -> {
                    RoomMember me = this.findMember(roomVo, userVo.getId()); // (2) 방 내부 내 정보 조회

                    List<ObjectId> messageBundleIds = roomVo.getMessageBundleIds(); // (3) 방 내부 메시지 번들 조회

                    long amountUnread = 0;
                    int mbIdx = messageBundleIds.size() - 1; // (4) messageBundleId는 room 생성시 자동 생성되기 때문에 무조건 하나는 있음
                    MessageBundleVo messageBundleVo = messageBundleService.find(messageBundleIds.get(mbIdx)); // (5) 가장 최신 메시지 번들
                    int recentMessageBundleCount = messageBundleVo.getCount();
                    if(mbIdx > 0 && recentMessageBundleCount == 0) { // (6) 가장 마지막 메시지 번들이 비어 있을때 바로 전 메시지 번들을 사용
                        mbIdx -= 1;
                        messageBundleVo = messageBundleService.find(messageBundleIds.get(mbIdx));
                    }

                    List<ObjectId> messageIds = messageBundleVo.getMessageIds();

                    ChatMessageVo recentChatMessageVo;
                    if(messageIds.size() > 0) recentChatMessageVo = chatMessageService.find(messageIds.get(messageIds.size() - 1));
                    // (7) 모든 채팅방은 첫 메시지건, 초대 메시지건 무조건 메시지를 하나 가지고 있어 구조상 불가능한 케이스지만 에러 방지를 위한 코드
                    else recentChatMessageVo = emptyChatMessageVo;


                    // Pagination을 제한적으로 사용하기 때문에 (전부 읽었을 수도 있고, 일정 갯수 넘어가면 탈출) 따로 limit 쿼리를 쓸 필요는 없어 보인다.
                    while(amountUnread < messageBundleLimit && mbIdx >= 0) {
                        // (8) 현재 메시지 번들에서 읽지 않은 메시지 수 계산
                        long partUnread = messageBundleVo.getMessageIds().parallelStream()
                                .map(chatMessageService::find)
                                .filter(chatMessage -> chatMessage.getSentAt().isAfter(me.getAwayAt())) // 인자보다 미래시간일때 true 반환
                                .count();
                        if(partUnread == 0) {
                            break; // 더 이상 안 읽은 메시지가 없음
                        }
                        else {
                            amountUnread += partUnread; // (9)이 메시지 번들에서 읽지 않은 메시지 수 더하기
                            if(amountUnread >= messageBundleLimit) amountUnread = messageBundleLimit;
                            messageBundleVo = messageBundleService.find(messageBundleIds.get(mbIdx--)); // (10) 이전 메시지 번들로 변경
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

    public RoomVo findPrivate(UserVo userVo, Long userId) { // 1:1 채팅방 조회
        if(userVo.getId().equals(userId)) throw TARGET_YOURSELF;
        Room room = roomRepository.findPrivate(userVo.getId(), userId).orElse(emptyRoom);
        return roomMapper.toVo(room);
    }

    public RoomVo modify(ObjectId roomId, RoomRequest request) { // U
        Room oldRoom = this.find(roomId);
        Room newRoom = roomMapper.toEntity(request);
        newRoom.setId(oldRoom.getId());
        return roomMapper.toVo(roomRepository.save(newRoom));
    }

    public RoomVo saveEnteredAt(ObjectId roomId, Long userId) { // 유저가 채팅방 소켓에 Connect된 시간 업데이트
        RoomVo roomVo = this.findById(roomId);
        RoomMember me = this.findMember(roomVo, userId);
        List<RoomMember> members = roomVo.getMembers();

        members.remove(me);
        me.setEnteredAt(LocalDateTime.now());
        members.add(me);
        return this.save(roomVo);
    }

    public RoomVo saveAwayAt(ObjectId roomId, Long userId) { // 유저가 채팅방 소켓에 Disconnect된 시간 업데이트
        RoomVo roomVo = this.findById(roomId);
        RoomMember me = this.findMember(roomVo, userId);
        List<RoomMember> members = roomVo.getMembers();

        members.remove(me);
        me.setAwayAt(LocalDateTime.now());
        members.add(me);
        return this.save(roomVo);
    }

    public RoomVo saveLeftAt(ObjectId roomId, Long userId) { // 유저가 채팅방에서 나간 시간 업데이트
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
            return this.save(roomVo);
        } else {
            if(members.size() == 0) {
                delete(roomVo);
                return roomMapper.toVo(emptyRoom);
            }
            else {
                return this.save(roomVo);
            }
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
