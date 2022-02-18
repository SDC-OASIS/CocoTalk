package com.cocotalk.chat.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@ToString
@Getter
@Setter
public class FileUploadRequest {
    private MultipartFile messageFile;
    private MultipartFile messageFileThumb;
}
