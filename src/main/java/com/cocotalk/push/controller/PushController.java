package com.cocotalk.push.controller;

import com.cocotalk.push.dto.RequestDto;
import com.cocotalk.push.service.FCMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/push")
@Slf4j
public class PushController {

    private final FCMService fcmService;

    @PostMapping
    public Mono<ResponseEntity> pushMessage(@RequestBody @Valid RequestDto requestDto) throws IOException {
        log.info(requestDto.getTargetToken() + " " +requestDto.getTitle() + " " + requestDto.getBody());
        fcmService.sendMessageTo(requestDto.getTargetToken(), requestDto.getTitle(), requestDto.getBody());
        return Mono.just(ResponseEntity.ok().build());
    }

}