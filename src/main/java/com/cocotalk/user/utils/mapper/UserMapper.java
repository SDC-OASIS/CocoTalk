package com.cocotalk.user.utils.mapper;

import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.domain.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    UserVo toVo(User user);
    User toEntity(UserVo userVo);
}
