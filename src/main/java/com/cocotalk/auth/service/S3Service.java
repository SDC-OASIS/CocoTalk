package com.cocotalk.auth.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
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

    public String uploadProfileImage(MultipartFile file, Long userId) throws IOException {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        return uploadImage(file, String.format("img/profile/%d_%s.%s", userId, LocalDateTime.now(), extension));
    }

    public String uploadThumbnail(MultipartFile file, Long userId) throws IOException {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        return uploadImage(file, String.format("img/profile/%d_%s_th.%s", userId, LocalDateTime.now(), extension));
    }

    public String uploadProfileBgImage(MultipartFile file, Long userId) throws IOException {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        return uploadImage(file, String.format("img/bg/%d_%s.%s", userId, LocalDateTime.now(), extension));
    }

    private String uploadImage(MultipartFile file, String filePath) throws IOException {
        amazonS3.putObject(new PutObjectRequest(bucket, filePath, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        log.info("[S3Service/uploadImage] : " + filePath + " is uploaded");
        return cloudFrontDomain+"/"+filePath;
    }

//    public void deleteMedia(Long userId, Long mediaId, MediaType mediaType) {
//        String filePath = "";
//        if(mediaType == MediaType.Video) {
//            filePath = "video/" + userId + "/" + mediaId.toString() + "/";
//        } else if (mediaType == MediaType.Sound) {
//            filePath = "sound/" + userId + "/" + mediaId.toString() + "/";
//        }
//
//        ObjectListing objectList = s3Client.listObjects(bucket, filePath);
//        List<S3ObjectSummary> objectSummeryList = objectList.getObjectSummaries();
//        String[] keysList = new String[objectSummeryList.size()];
//        int count = 0;
//        for (S3ObjectSummary summery : objectSummeryList) {
//            keysList[count++] = summery.getKey();
//        }
//        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket).withKeys(keysList);
//        s3Client.deleteObjects(deleteObjectsRequest);
//        log.info(filePath + " removed");
//    }

}