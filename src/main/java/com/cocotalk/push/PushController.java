package com.cocotalk.push;

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

    @GetMapping("/")
    Flux<String> hello() {
        return Flux.just("Hello", "World");
    }

    @GetMapping("/stream")
    Flux<Map<String, Integer>> stream() {
        Stream<Integer> stream = Stream.iterate(0, i -> i + 1); // Java8의 무한 Stream
        return Flux.fromStream(stream.limit(10))
                .map(i -> Collections.singletonMap("value", i));
    }

    @PostMapping("/fcm")
    public Mono<ResponseEntity> pushMessage(@RequestBody @Valid RequestDto requestDto) throws IOException {
        log.info(requestDto.getTargetToken() + " " +requestDto.getTitle() + " " + requestDto.getBody());
        fcmService.sendMessageTo(requestDto.getTargetToken(), requestDto.getTitle(), requestDto.getBody());
        return Mono.just(ResponseEntity.ok().build());
    }
}