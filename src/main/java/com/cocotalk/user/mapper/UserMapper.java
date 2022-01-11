package com.cocotalk.user.mapper;

import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.domain.vo.UserVo;
import com.cocotalk.user.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    UserResponse toDto(User user);
    User toEntity(UserVo userVo);
}
