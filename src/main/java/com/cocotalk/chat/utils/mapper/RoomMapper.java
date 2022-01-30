package com.cocotalk.chat.utils.mapper;

import com.cocotalk.chat.domain.entity.room.Room;
import com.cocotalk.chat.domain.vo.RoomVo;
import com.cocotalk.chat.dto.request.RoomRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = RoomMemberMapper.class
)

public interface RoomMapper {
    RoomVo toVo(Room room);
    Room toEntity(RoomRequest roomRequest);
}
