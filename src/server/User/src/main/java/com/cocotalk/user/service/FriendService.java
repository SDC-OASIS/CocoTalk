package com.cocotalk.user.service;

import com.cocotalk.user.domain.FriendType;
import com.cocotalk.user.domain.entity.Friend;
import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.domain.vo.FriendInfoVo;
import com.cocotalk.user.domain.vo.FriendVo;
import com.cocotalk.user.domain.vo.UserVo;
import com.cocotalk.user.dto.request.FriendAddRequest;
import com.cocotalk.user.dto.request.FriendHideRequest;
import com.cocotalk.user.dto.request.FriendsAddRequest;
import com.cocotalk.user.exception.CustomError;
import com.cocotalk.user.exception.CustomException;
import com.cocotalk.user.repository.FriendRepository;
import com.cocotalk.user.repository.UserRepository;
import com.cocotalk.user.utils.mapper.FriendMapper;
import com.cocotalk.user.utils.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final FriendMapper friendMapper;
    private final UserMapper userMapper;

    public static final CustomException INVALID_USER_ID =
            new CustomException(CustomError.BAD_REQUEST, "해당 userId를 갖는 유저가 존재하지 않습니다.");

    public static final CustomException FRIEND_ALREADY_EXIST =
            new CustomException(CustomError.BAD_REQUEST, "해당 userId를 갖는 친구가 이미 존재합니다.");

    public static final CustomException INVALID_TO_UID =
            new CustomException(CustomError.BAD_REQUEST, "해당 userId를 갖는 친구가 존재하지 않습니다.");

    public static final User EMPTY_USER = new User();
    public static final Friend EMPTY_FRIEND = new Friend();

    Comparator<FriendInfoVo> orderByUserName = (o1, o2) ->
            o1.getFriend().getUsername().compareTo(o2.getFriend().getUsername());

    @Transactional
    public FriendVo add(User fromUser, FriendAddRequest request){
        // (1) 친구 존재 확인
        if(!friendRepository.existsByFromUserIdAndToUserId(fromUser.getId(), request.getToUid())) {
            // (2) 친구 유저 정보 조회
            User toUser = userRepository.findById(request.getToUid()).orElseThrow(() -> INVALID_USER_ID);
            // (3) 친구 생성 후 저장
            Friend friend = friendRepository.save(Friend.builder()
                    .fromUser(fromUser)
                    .toUser(toUser)
                    .hidden(false)
                    .build());
            return friendMapper.toVo(friend);
        } else {
            // (4) 친구 이미 존재한다면 예외 반환
            throw FRIEND_ALREADY_EXIST;
        }
    }

    @Transactional
    public List<FriendVo> addList(User fromUser, FriendsAddRequest request){
        // (1) 추가하려는 친구들의 userId 추출
        List<Long> toUidList = request.getToUidList()
                .stream()
                .distinct() // (2) 중복 입력 제거
                .filter(toUid -> !friendRepository.existsByFromUserIdAndToUserId(fromUser.getId(), toUid)) // (3) 중복 삽입 방지
                .collect(Collectors.toList());
        // (4) 추가하려는 친구들의 유저 정보 조회 
        List<User> toUserList = userRepository.findAllById(toUidList);
        // (5) 친구 리스트 생성 후 저장
        List<Friend> friendList = toUserList.stream()
                .map(toUser -> Friend.builder()
                        .fromUser(fromUser)
                        .toUser(toUser)
                        .hidden(false)
                        .build())
                .collect(Collectors.toList());
        return friendRepository.saveAll(friendList).stream()
                .map(friendMapper::toVo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FriendInfoVo findByCid(User fromUser, String cid) {
        // (1) 자신과 동일한 cid를 가지고 있는지 확인
        if(fromUser.getCid().equals(cid)) {
            UserVo userVo = userMapper.toVo(userRepository.findById(fromUser.getId()).orElse(EMPTY_USER));
            return FriendInfoVo.builder().friend(userVo).type(FriendType.YOURSELF.ordinal()).build();
        } else {
            User toUser = userRepository.findByCid(cid).orElse(EMPTY_USER);
            Friend friend = friendRepository.findByFromUserIdAndToUserId(fromUser.getId(), toUser.getId()).orElse(EMPTY_FRIEND);
            // (2) 이미 존재하는 친구인지 확인
            if(friend.getId() == null) {
                return FriendInfoVo.builder().friend(userMapper.toVo(toUser)).type(FriendType.PASSIVE.ordinal()).build();
            } else {
                return FriendInfoVo.builder().friend(userMapper.toVo(toUser)).type(FriendType.ACTIVE.ordinal()).build();
            }
        }
    }


    @Transactional(readOnly = true)
    public List<FriendInfoVo> findAll(User fromUser) {
        // (1) 내가 친구 추가한 사람들
        List<FriendInfoVo> myFriends = friendRepository.findByFromUserIdAndHiddenIsFalse(fromUser.getId()).stream()
                .map(friend -> userMapper.toVo(friend.getToUser()))
                .map(userVo -> FriendInfoVo.builder()
                        .friend(userVo)
                        .type(FriendType.ACTIVE.ordinal())
                        .build())
                .collect(Collectors.toList());

        // (2) 나를 친구 추가한 사람들
        List<FriendInfoVo> addedMe = friendRepository.findByToUserId(fromUser.getId()).stream() //
                .map(friend -> userMapper.toVo(friend.getFromUser()))
                .map(userVo -> FriendInfoVo.builder()
                        .friend(userVo)
                        .type(FriendType.PASSIVE.ordinal())
                        .build())
                .collect(Collectors.toList());

        // (3) 서로 같이 추가한 친구 제거 -> 친구 리스트 합치기
        addedMe.removeAll(myFriends);
        myFriends.addAll(addedMe);

        // (5) 이름 별로 정렬
        myFriends.sort(orderByUserName);

        return myFriends;
    }

    @Transactional(readOnly = true)
    public List<UserVo> findHiddenFriends(User fromUser) {
        List<Friend> hiddenFriends = friendRepository.findByFromUserIdAndHiddenIsTrue(fromUser.getId());
        return hiddenFriends.stream()
                .map(Friend::getToUser)
                .map(userMapper::toVo)
                .sorted(Comparator.comparing(UserVo::getUsername))
                .collect(Collectors.toList());
    }

    @Transactional
    public FriendVo hide(User fromUser, FriendHideRequest request) {
        Friend friend = friendRepository.findByFromUserIdAndToUserId(fromUser.getId(), request.getToUid())
                .orElseThrow(() -> INVALID_TO_UID);
        friend.setHidden(request.isHidden());
        return friendMapper.toVo(friend);
    }

    @Transactional
    public String delete(User fromUser, Long toUid) {
        User toUser = userRepository.findById(toUid).orElseThrow(() -> INVALID_USER_ID);
        Friend friend = friendRepository.findByFromUserIdAndToUserId(fromUser.getId(), toUid)
                .orElseThrow(() -> INVALID_TO_UID);
        String message = String.format("%s 님을 친구에서 삭제했습니다.", toUser.getCid());
        friendRepository.delete(friend);
        return message;
    }
}
