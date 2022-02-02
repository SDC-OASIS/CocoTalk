package com.cocotalk.chat.utils.mapper;

import com.cocotalk.chat.domain.entity.message.ChatMessage;
import com.cocotalk.chat.domain.entity.message.InviteMessage;
import com.cocotalk.chat.domain.vo.ChatMessageVo;
import com.cocotalk.chat.domain.vo.InviteMessageVo;
import com.cocotalk.chat.dto.request.ChatMessageRequest;
import com.cocotalk.chat.dto.request.InviteMessageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MessageMapper {
    ChatMessageVo toVo(ChatMessage chatMessage);
    ChatMessage toEntity(ChatMessageVo chatMessageVo);
    ChatMessage toEntity(ChatMessageRequest chatMessageRequest);

    InviteMessageVo toVo(InviteMessage inviteMessage);
    InviteMessage toEntity(InviteMessageVo inviteMessageVo);
    InviteMessage toEntity(InviteMessageRequest inviteMessageRequest);
}
