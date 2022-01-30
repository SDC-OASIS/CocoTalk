package com.cocotalk.chat.utils.mapper;

import com.cocotalk.chat.domain.entity.room.RoomMember;
import com.cocotalk.chat.dto.response.RoomMemberResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public interface RoomMemberMapper {
    RoomMemberResponse toDto(RoomMember roomMember);
}
