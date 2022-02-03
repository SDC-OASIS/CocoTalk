package com.cocotalk.push.controller;

import com.cocotalk.push.dto.push.PushInfoInput;
import com.cocotalk.push.service.FCMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.IOException;

@Tag(name = "푸시 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PushController {

    private final FCMService fcmService;

    @Operation(summary = "token 리스트로 메시지 보내기")
    @PostMapping("/tokens")
    public Mono<ResponseEntity> pushMessage(@RequestBody @Valid PushInfoInput pushInfoInput) throws IOException {
        log.info(pushInfoInput.getTokenList() + " " +pushInfoInput.getTitle() + " " + pushInfoInput.getBody());
        fcmService.sendByTokenList(pushInfoInput.getTokenList(), pushInfoInput.getTitle(), pushInfoInput.getBody());
        return Mono.just(ResponseEntity.ok().build());
    }

}