package com.cocotalk.user.service;

import com.cocotalk.user.domain.entity.Friend;
import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.domain.vo.FriendVo;
import com.cocotalk.user.domain.vo.UserVo;
import com.cocotalk.user.dto.exception.GlobalError;
import com.cocotalk.user.dto.exception.GlobalException;
import com.cocotalk.user.dto.request.FriendAddRequest;
import com.cocotalk.user.utils.mapper.FriendMapper;
import com.cocotalk.user.utils.mapper.UserMapper;
import com.cocotalk.user.repository.FriendRepository;
import com.cocotalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final FriendMapper friendMapper;
    private final UserMapper userMapper;

    public static final GlobalException INVALID_ID = new GlobalException(GlobalError.BAD_REQUEST, "해당 id를 갖는 유저가 존재하지 않습니다.");

    public FriendVo add(User fromUser, FriendAddRequest request){
        User toUser =  userRepository.findById(request.getToUid()).orElseThrow(() -> INVALID_ID);
        Friend friend = friendRepository.save(Friend.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build());
        return friendMapper.toVo(friend);
    }

    public List<UserVo> find(User fromUser) {
        List<Friend> friends = friendRepository.findByFromUserId(fromUser.getId());
        return friends.stream()
                .map(friend -> userMapper.toVo(friend.getToUser()))
                .collect(Collectors.toList());
    }
}
