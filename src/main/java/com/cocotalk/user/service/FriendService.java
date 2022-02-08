package com.cocotalk.user.service;

import com.cocotalk.user.domain.entity.Friend;
import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.domain.vo.FriendVo;
import com.cocotalk.user.domain.vo.UserVo;
import com.cocotalk.user.dto.request.FriendHideRequest;
import com.cocotalk.user.exception.CustomError;
import com.cocotalk.user.exception.CustomException;
import com.cocotalk.user.dto.request.FriendAddRequest;
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

    public static final CustomException INVALID_USERID =
            new CustomException(CustomError.BAD_REQUEST, "해당 userId를 갖는 유저가 존재하지 않습니다.");

    private static final Comparator<UserVo> orderByUserName = Comparator.comparing(UserVo::getUsername);

    @Transactional
    public FriendVo add(User fromUser, FriendAddRequest request){
        User toUser =  userRepository.findById(request.getToUid()).orElseThrow(() -> INVALID_USERID);
        Friend friend = friendRepository.save(Friend.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .hidden(false)
                .build());
        return friendMapper.toVo(friend);
    }

    @Transactional(readOnly = true)
    public List<UserVo> find(User fromUser) {
        List<Friend> friends = friendRepository.findByFromUserIdAndHiddenIsFalse(fromUser.getId());
        return friends.stream()
                .map(friend -> userMapper.toVo(friend.getToUser()))
                .sorted(orderByUserName)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserVo> findHiddenFriends(User fromUser) {
        List<Friend> hiddenFriends = friendRepository.findByFromUserIdAndHiddenIsTrue(fromUser.getId());
        return hiddenFriends.stream()
                .map(friend -> userRepository.findById(friend.getToUser().getId()).orElseThrow(() -> INVALID_USERID))
                .map(userMapper::toVo)
                .sorted(orderByUserName)
                .collect(Collectors.toList());
    }

    @Transactional
    public FriendVo hide(User fromUser, FriendHideRequest request) {
        Friend friend = friendRepository.findByFromUserIdAndToUserId(fromUser.getId(), request.getToUid());
        friend.setHidden(request.isHidden());
        return friendMapper.toVo(friend);
    }

    @Transactional
    public String delete(User fromUser, Long toUid) {
        User toUser = userRepository.findById(toUid).orElseThrow(() -> INVALID_USERID);
        Friend friend = friendRepository.findByFromUserIdAndToUserId(fromUser.getId(), toUser.getId());
        String message = String.format("%s 님을 친구에서 삭제했습니다.", toUser.getCid());
        friendRepository.delete(friend);
        return message;
    }
}
