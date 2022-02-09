package com.cocotalk.chat.controller;

import com.cocotalk.chat.domain.vo.ChatMessageVo;
import com.cocotalk.chat.dto.response.CustomResponse;
import com.cocotalk.chat.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "메시지 API")
@RequiredArgsConstructor
@RequestMapping(value = "/messages")
public class MessageController {
    private final ChatMessageService chatMessageService;

    @Value(value = "${cocotalk.message-paging-size}")
    private int messagePagingSize;

    @GetMapping
    @Operation(summary = "채팅방 메시지 페이징")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<List<ChatMessageVo>>> findRoomList(@RequestParam ObjectId roomid,
                                                                            @RequestParam ObjectId bundleid,
                                                                            @RequestParam int count){
        List<ChatMessageVo> data = chatMessageService.findMessagePage(roomid, bundleid, count, messagePagingSize);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }
}