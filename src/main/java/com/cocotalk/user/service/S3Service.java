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
import java.util.List;

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
        //프로필 업로드
        String extension = StringUtils.getFilenameExtension(img.getOriginalFilename());
        String imgPath = "user_profile/" + userId + "/profile/" + LocalDateTime.now();
        String originUrl = uploadFile(img,imgPath+"."+extension);
        //썸네일 업로드
        String thumbUrl = uploadFile(imgThumb, imgPath+"_th."+extension);
        log.info("[S3Service/originUrl] : "+originUrl);
        log.info("[S3Service/thumbUrl] : "+thumbUrl);
        return originUrl;
    }

    public String uploadProfileBg(MultipartFile img, Long userId) {
        String extension = StringUtils.getFilenameExtension(img.getOriginalFilename());
        String filePath = "user_profile/" + userId + "/bg/" + LocalDateTime.now() + "."+ extension;
        return uploadFile(img, filePath);
    }

    public void deleteProfileImg(Long userId) {
        String dirPath = "user_profile/" + userId +"/profile/";
        deleteFile(dirPath);
    }

    public void deleteProfileBg(Long userId) {
        String dirPath = "user_profile/" + userId +"/bg/";
        deleteFile(dirPath);
    }

    private String uploadFile(MultipartFile file, String filePath) {
        try {
            amazonS3.putObject(new PutObjectRequest(bucket, filePath, file.getInputStream(), null)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            log.info("[S3Service/uploadImage] : " + filePath + " is uploaded");
            return cloudFrontDomain+"/"+filePath;
        } catch (IOException e) {
            throw new CustomException(CustomError.UNKNOWN);
        }
    }

    private void deleteFile(String filePath) {
        ObjectListing objectList = amazonS3.listObjects(bucket, filePath);
        List<S3ObjectSummary> objectSummeryList = objectList.getObjectSummaries();

        if(objectSummeryList.size()==0) return;

        String[] keysList = new String[objectSummeryList.size()];
        int count = 0;
        for (S3ObjectSummary summery : objectSummeryList) {
            keysList[count++] = summery.getKey();
        }
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket).withKeys(keysList);
        amazonS3.deleteObjects(deleteObjectsRequest);
        log.info("[deleteFile/filePath]" + filePath + "is removed");
    }

}