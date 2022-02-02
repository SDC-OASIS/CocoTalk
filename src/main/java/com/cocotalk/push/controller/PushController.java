package com.cocotalk.push.controller;

import com.cocotalk.push.dto.push.PushInfoInput;
import com.cocotalk.push.service.FCMService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.IOException;

@Api(tags = "푸시 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/push")
@Slf4j
public class PushController {

    private final FCMService fcmService;

//    @ApiOperation(value = "token으로 메시지 보내기")
//    @PostMapping("/token")
//    public Mono<ResponseEntity> pushMessage(@RequestBody @Valid PushInfoInput requestDto) throws IOException {
//        log.info(requestDto.getTargetToken() + " " +requestDto.getTitle() + " " + requestDto.getBody());
//        fcmService.sendByToken(requestDto.getTargetToken(), requestDto.getTitle(), requestDto.getBody());
//        return Mono.just(ResponseEntity.ok().build());
//    }

    @ApiOperation(value = "token 리스트로 메시지 보내기")
    @PostMapping("/tokens")
    public Mono<ResponseEntity> pushMessage(@RequestBody @Valid PushInfoInput pushInfoInput) throws IOException {
        log.info(pushInfoInput.getTokenList() + " " +pushInfoInput.getTitle() + " " + pushInfoInput.getBody());
        fcmService.sendByTokenList(pushInfoInput.getTokenList(), pushInfoInput.getTitle(), pushInfoInput.getBody());
        return Mono.just(ResponseEntity.ok().build());
    }


}