package com.cocotalk.entity.mapper;

import com.cocotalk.dto.signup.SignupInput;
import com.cocotalk.dto.signup.SignupOutput;
import com.cocotalk.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(SignupInput signupInput);
    SignupOutput toDto(User user);

}
