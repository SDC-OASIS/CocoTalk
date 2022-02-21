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

/**
 *
 * S3에 파일을 올리는 서비스
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 채팅 메시지에 들어갈 파일을 S3에 업로드합니다.
     * @param file S3에 업로드할 파일
     * @param thumbnail S3에 업로드할 파일의 썸네일 (썸네일 파일이 있는 경우에만 업로드합니다)
     * @param roomId 채팅이 올라가는 roomId
     * @param userId 채팅을 올린 userId
     *
     * @return 업로드된 file의 url
     */
    public String uploadMessageFile(MultipartFile file, MultipartFile thumbnail, String roomId, Long userId) {
        //프로필 업로드
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filePath = "chat/" + roomId + "/"+ userId +"_" + LocalDateTime.now();
        String fileUrl = uploadFile(file,filePath+"."+extension);
        //썸네일 업로드, 썸네일 확장자는 .jpeg
        if(thumbnail != null && !thumbnail.isEmpty()) {
            if(file.getContentType().contains("image")) {
                uploadFile(thumbnail, filePath+"_th.jpeg");
            } else if(file.getContentType().contains("video")){
                uploadFile(thumbnail, filePath+"_th.jpeg");
            }
        }
        return fileUrl;
    }

    /**
     * S3에 파일을 업로드합니다
     * @param file S3에 업로드할 파일
     * @param filePath S3에 업로드될 경로
     *
     * @return 업로드된 file의 url
     */
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

}