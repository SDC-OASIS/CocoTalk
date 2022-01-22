package com.cocotalk.user.mapper;

import com.cocotalk.user.domain.entity.Friend;
import com.cocotalk.user.domain.vo.FriendVo;
import com.cocotalk.user.dto.response.FriendResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = UserMapper.class
)
public interface FriendMapper {
    Friend toEntity(FriendVo friendVo);
    FriendVo toVo(Friend friend);

    FriendResponse toDto(Friend friend);
}
