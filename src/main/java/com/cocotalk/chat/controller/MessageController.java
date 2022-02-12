package com.cocotalk.chat.controller;

import com.cocotalk.chat.domain.vo.ChatMessageVo;
import com.cocotalk.chat.domain.vo.UserVo;
import com.cocotalk.chat.dto.response.CustomResponse;
import com.cocotalk.chat.service.ChatMessageService;
import com.cocotalk.chat.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "메시지 API")
@RequiredArgsConstructor
@RequestMapping(value = "/messages")
public class MessageController {

    private final ChatMessageService chatMessageService;
    private final S3Service s3Service;

    @Value(value = "${cocotalk.message-paging-size}")
    private int messagePagingSize;

    /**
     * 채팅방 메시지 페이징 [GET] /messages
     *
     * @param roomId 메시지를 페이징할 채팅방의 ObjectId
     * @param bundleId 메시지를 페이징할 메시지 번들의 ObjectId
     * @param count 마지막으로 조회한 메시지 번들의 count
     * @return ResponseEntity<CustomResponse<List<ChatMessageVo>>> 페이징한 채팅방의 메시지 정보가 포함됩니다.
     */
    @GetMapping
    @Operation(summary = "채팅방 메시지 페이징")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<List<ChatMessageVo>>> findRoomList(@RequestParam ObjectId roomId,
                                                                            @RequestParam ObjectId bundleId,
                                                                            @RequestParam int count){
        List<ChatMessageVo> data = chatMessageService.findMessagePage(roomId, bundleId, count, messagePagingSize);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * 채팅방에 file 업로드 [GET] /messages/file
     * @param userVo 파일 업로드를 요청한 유저의 정보
     * @param roomId 채팅방의 id
     * @param file 업로드 요청할 파일
     * @return ResponseEntity<CustomResponse<List<ChatMessageVo>>> 페이징한 채팅방의 메시지 정보가 포함됩니다.
     */
    @PostMapping("/file")
    @Operation(summary = "채팅방 파일 업로드")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<String>> findRoomList(@Parameter(hidden = true) UserVo userVo,
                                                                            @RequestParam ObjectId roomId,
                                                                            @RequestParam MultipartFile file){
        String data = s3Service.uploadMessageFile(file, roomId.toString(), userVo.getId());
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }
}