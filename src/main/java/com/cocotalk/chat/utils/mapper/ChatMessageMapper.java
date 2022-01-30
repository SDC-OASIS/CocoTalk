package com.cocotalk.chat.utils.mapper;

import com.cocotalk.chat.domain.entity.message.ChatMessage;
import com.cocotalk.chat.domain.vo.ChatMessageVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ChatMessageMapper {
    ChatMessageVo toVo(ChatMessage chatMessage);
    ChatMessage toEntity(ChatMessageVo chatMessageVo);
}
