package com.cocotalk.auth.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.cocotalk.auth.dto.common.response.ResponseStatus;
import com.cocotalk.auth.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadProfileImg(MultipartFile img, MultipartFile imgThumb, Long userId) {
        //썸네일 업로드
        String extensionThumb = StringUtils.getFilenameExtension(imgThumb.getOriginalFilename());
        uploadFile(imgThumb, String.format("user_profile/" + userId + "/profile/" + LocalDateTime.now() + "_th."+ extensionThumb));
        //프로필 업로드
        String extension = StringUtils.getFilenameExtension(img.getOriginalFilename());
        return uploadFile(img, String.format("user_profile/" + userId + "/profile/" + LocalDateTime.now() + "."+ extension));
    }

    private String uploadFile(MultipartFile file, String filePath) {
        try {
            amazonS3.putObject(new PutObjectRequest(bucket, filePath, file.getInputStream(), null)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            log.info("[S3Service/uploadImage] : " + filePath + " is uploaded");
            return cloudFrontDomain+"/"+filePath;
        } catch (IOException e) {
            throw new CustomException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }


}