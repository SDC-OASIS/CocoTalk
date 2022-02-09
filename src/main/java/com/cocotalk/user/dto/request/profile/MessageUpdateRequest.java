package com.cocotalk.user.dto.request.profile;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MessageUpdateRequest {
    private String message;
}
