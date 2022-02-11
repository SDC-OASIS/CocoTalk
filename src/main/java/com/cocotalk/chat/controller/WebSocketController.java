package com.cocotalk.chat.controller;

import com.cocotalk.chat.utils.WebSocketUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final WebSocketUtils websocketUtil;

    @GetMapping("/ws")
    public void test(@RequestParam(value = "value") String value) throws Exception {
        log.info("value : {}", value);
        //웹소켓 메세지 보내기
        websocketUtil.send(value);
    }
}
