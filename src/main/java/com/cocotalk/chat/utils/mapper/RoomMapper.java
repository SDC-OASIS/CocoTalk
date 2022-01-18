package com.cocotalk.chat.utils.mapper;

import com.cocotalk.chat.document.Room;
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

//    @Mapping(target = "memberPK", ignore = true)
//    @Mapping(target = "messagePK", ignore = true)
//    @Mapping(target = "noticePK", ignore = true)
    Room toEntity(RoomRequest roomRequest);
}
