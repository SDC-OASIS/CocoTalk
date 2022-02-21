package com.cocotalk.user.dto.request.profile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@ToString
@Getter
@Setter
public class BgUpdateRequest {
    private MultipartFile bgImg;
}
