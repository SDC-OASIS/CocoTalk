package com.cocotalk.chat.domain.vo;

import com.cocotalk.chat.dto.request.RoomMemberRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InviteMessageVo extends ChatMessageVo {
    private List<RoomMemberRequest> invitees;
}
