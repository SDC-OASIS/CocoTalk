package com.cocotalk.user.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class ProfileUpdateRequest {
    @NotNull
    private MultipartFile profileImg;
    @NotNull
    private MultipartFile profileImgThumb;
}
