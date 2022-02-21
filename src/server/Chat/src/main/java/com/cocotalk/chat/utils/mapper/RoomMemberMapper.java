package com.cocotalk.chat.utils.mapper;

import com.cocotalk.chat.domain.entity.room.RoomMember;
import com.cocotalk.chat.domain.vo.RoomMemberVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public interface RoomMemberMapper {
    RoomMemberVo toVo(RoomMember roomMember);
}
