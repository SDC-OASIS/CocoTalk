package com.cocotalk.chat.utils.mapper;

import com.cocotalk.chat.document.room.Room;
import com.cocotalk.chat.model.request.RoomRequest;
import com.cocotalk.chat.model.response.RoomResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = RoomMemberMapper.class
)

public interface RoomMapper {

    RoomResponse toDto(Room room);

    Room toEntity(RoomRequest roomRequest);
}
