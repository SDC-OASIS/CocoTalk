package com.cocotalk.chat.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
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

    public String uploadMessageFile(MultipartFile file, MultipartFile thumbnail, String roomId, Long userId) {
        //프로필 업로드
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filePath = "chat/" + roomId + "/"+ userId +"_" + LocalDateTime.now();
        String fileUrl = uploadFile(file,filePath+"."+extension);
        //썸네일 업로드
        if(thumbnail != null && !thumbnail.isEmpty()) {
            if(file.getContentType().contains("image")) {   // 이미지 파일이면 썸네일 확장자 그대로 감
                uploadFile(thumbnail, filePath+"_th."+extension);
            } else if(file.getContentType().contains("video")){  // 동영상 파일이면 썸네일 확장자는 jpg 감
                uploadFile(thumbnail, filePath+"_th.jpeg");
            }
        }
        return fileUrl;
    }

    private String uploadFile(MultipartFile file, String filePath) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            // text 파일일 경우 UTF-8로 인코딩
            if(file.getContentType().equals("text/plain")) {
                metadata.setContentType("text/plain;charset=utf-8");
            }
            else{
                metadata.setContentType(file.getContentType());
            }
            log.info("metadata:" +metadata);
            log.info("file metadata: "+file.getContentType());
            amazonS3.putObject(new PutObjectRequest(bucket, filePath, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            log.info("[S3Service/uploadImage] : " + filePath + " is uploaded");
            return cloudFrontDomain+"/"+filePath;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("[S3Service/uploadFile] : S3에 파일을 업로드하는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.INPUT_OUTPUT, e);
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