package com.cocotalk.chat.service;

import com.cocotalk.chat.document.room.RoomMember;
import com.cocotalk.chat.repository.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomMemberService {
    private final RoomMemberRepository roomMemberRepository;

    public List<RoomMember> join(List<RoomMember> members){
        return roomMemberRepository.saveAll(members);
    }
}
