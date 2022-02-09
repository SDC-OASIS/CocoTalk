package com.cocotalk.user.dto.request.profile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@ToString
@Getter
@Setter
public class ImgUpdateRequest {
    private MultipartFile profileImg;
    private MultipartFile profileImgThumb;
}
