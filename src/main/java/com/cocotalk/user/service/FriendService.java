package com.cocotalk.user.service;

import com.cocotalk.user.domain.entity.Friend;
import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.dto.request.FriendAddRequest;
import com.cocotalk.user.dto.response.FriendResponse;
import com.cocotalk.user.mapper.FriendMapper;
import com.cocotalk.user.repository.FriendRepository;
import com.cocotalk.user.repository.UserRepository;
import com.cocotalk.user.support.GlobalError;
import com.cocotalk.user.support.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;

    public FriendResponse add(FriendAddRequest request){
        User fromUser = userRepository.findById(request.getFromUid())
                .orElseThrow(() -> new GlobalException(GlobalError.USER_NOT_EXIST));
        User toUser =  userRepository.findById(request.getToUid())
                .orElseThrow(() -> new GlobalException(GlobalError.USER_NOT_EXIST));
        Friend friend = friendRepository.save(Friend.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build());
        return friendMapper.toDto(friend);
    }
}
