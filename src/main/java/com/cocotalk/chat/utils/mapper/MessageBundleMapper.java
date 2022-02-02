package com.cocotalk.chat.utils.mapper;

import com.cocotalk.chat.domain.entity.message.MessageBundle;
import com.cocotalk.chat.domain.vo.MessageBundleVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MessageBundleMapper {
    MessageBundle toEntity(MessageBundleVo messageBundleVo);
    MessageBundleVo toVo (MessageBundle messageBundle);
}
