package com.cocotalk.user.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.cocotalk.user.exception.CustomError;
import com.cocotalk.user.exception.CustomException;
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

    public String uploadProfileImage(MultipartFile file, Long userId) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        return uploadImage(file, String.format("img/profile/%d_%s.%s", userId, LocalDateTime.now(), extension));
    }

    public String uploadProfileThumb(MultipartFile file, Long userId) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        return uploadImage(file, String.format("img/profile/%d_%s_th.%s", userId, LocalDateTime.now(), extension));
    }

    public String uploadProfileBgImage(MultipartFile file, Long userId) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        return uploadImage(file, String.format("img/bg/%d_%s.%s", userId, LocalDateTime.now(), extension));
    }

    private String uploadImage(MultipartFile file, String filePath) {
        try {
            amazonS3.putObject(new PutObjectRequest(bucket, filePath, file.getInputStream(), null)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            log.info("[S3Service/uploadImage] : " + filePath + " is uploaded");
            return cloudFrontDomain+"/"+filePath;
        } catch (IOException e) {
            throw new CustomException(CustomError.UNKNOWN);
        }
    }
}