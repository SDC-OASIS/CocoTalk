package com.cocotalk.auth.entity.mapper;

import com.cocotalk.auth.dto.signup.SignupInput;
import com.cocotalk.auth.entity.User;
import com.cocotalk.auth.dto.signup.SignupOutput;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(SignupInput signupInput);
    SignupOutput toDto(User user);

}
