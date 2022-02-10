package com.cocotalk.user.service;

import com.cocotalk.user.domain.entity.Friend;
import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.domain.vo.FriendListVo;
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

    Comparator<FriendListVo> orderByUserName = (o1, o2) ->
            o1.getFriend().getUsername().compareTo(o2.getFriend().getUsername());

    @Transactional
    public FriendVo add(User fromUser, FriendAddRequest request){
        if(!friendRepository.existsByFromUserIdAndToUserId(fromUser.getId(), request.getToUid())) {
            User toUser = userRepository.findById(request.getToUid()).orElseThrow(() -> INVALID_USER_ID);
            Friend friend = friendRepository.save(Friend.builder()
                    .fromUser(fromUser)
                    .toUser(toUser)
                    .hidden(false)
                    .build());
            return friendMapper.toVo(friend);
        } else {
            throw FRIEND_ALREADY_EXIST;
        }
    }

    @Transactional
    public List<FriendVo> addList(User fromUser, FriendsAddRequest request){
        List<Long> toUidList = request.getToUidList()
                .stream()
                .distinct() // 중복 입력 필터링
                .filter(toUid -> !friendRepository.existsByFromUserIdAndToUserId(fromUser.getId(), toUid)) // 중복 삽입 방지
                .collect(Collectors.toList());
        List<User> toUserList = userRepository.findAllById(toUidList);
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
    public List<FriendListVo> findAll(User fromUser) {
        // 내가 친구 추가한 사람들
        List<FriendListVo> myFriends = friendRepository.findByFromUserIdAndHiddenIsFalse(fromUser.getId()).stream()
                .map(friend -> userMapper.toVo(friend.getToUser()))
                .map(userVo -> FriendListVo.builder()
                        .friend(userVo)
                        .status(0)
                        .build())
                .collect(Collectors.toList());

        // 나를 친구 추가한 사람들
        List<FriendListVo> addedMe = friendRepository.findByToUserId(fromUser.getId()).stream() //
                .map(friend -> userMapper.toVo(friend.getFromUser()))
                .map(userVo -> FriendListVo.builder()
                        .friend(userVo)
                        .status(1)
                        .build())
                .collect(Collectors.toList());

        addedMe.removeAll(myFriends);
        myFriends.addAll(addedMe);
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
