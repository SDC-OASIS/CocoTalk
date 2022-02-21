package com.cocotalk.push.controller;

import com.cocotalk.push.dto.common.ClientInfo;
import com.cocotalk.push.dto.device.*;
import com.cocotalk.push.entity.Device;
import com.cocotalk.push.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@Tag(name = "디바이스 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/device")
public class DeviceController {

    private final DeviceService deviceService;

    /**
     * PK로 DEVICE 정보 조회 API [GET] /device/{id}
     *
     * @param id device pk
     * @return 조회한 device 정보
     */
    @Operation(summary  = "pk로 device 찾기")
    @GetMapping("/{id}")
    public Mono<Device> find(@PathVariable long id) {
        log.info("[device/post] find device");
        return deviceService.find(id);
    }

    /**
     * 조건으로 DEVICE 정보 조회 API [GET] /device
     * (userId) or (userId and Type)의 조건으로 device 정보들을 찾습니다.
     *
     * @param selectInput device를 조회할 조건이 담긴 요청 모델
     * @return 조회한 device들의 정보
     */
    @Operation(summary = "(userId) 혹은 (userId,type)으로 device 정보 찾기")
    @GetMapping
    public Flux<Device> findByUserId(@Valid SelectInput selectInput) {
        log.info("[device/post] findByUserId device");
        return deviceService.findByOptions(selectInput);
    }

    /**
     * DEVICE의 FCM TOKEN 생성/갱신 API [POST] /device
     * client Type(MOBILE/WEB)과 userId가 일치하는 fcmToken을 갱신합니다. (없는 경우 생성합니다)
     *
     * @param clientInfo 요청자의 client 정보 ( MOBILE/WEB을 별도로 관리하기 위해 필요 )
     * @param saveInput 갱신될 userId와  갱신할 fcmToken이 담긴 요청 모델
     * @return 갱신된 device 정보
     */
    @Operation(summary = "device 생성/갱신하기")
    @PostMapping
    public Mono<Device> save(@Parameter(hidden = true) ClientInfo clientInfo, @RequestBody @Valid SaveInput saveInput) {
        log.info("[device/post] save device");
        return deviceService.save(clientInfo, saveInput);
    }

    /**
     *
     * DEVICE의 삭제 API [DELETE] /device
     * client Type(MOBILE/WEB)과 userId가 일치하는 device 정보를 삭제합니다
     *
     * @param clientInfo 요청자의 client 정보 ( MOBILE/WEB을 별도로 관리하기 위해 필요 )
     * @param deleteInput 삭제할 userId
     */
    @Operation(summary = "device 삭제하기")
    @DeleteMapping
    public void delete(@Parameter(hidden = true)  ClientInfo clientInfo, @Valid DeleteInput deleteInput) {
        log.info("[device/delete] delete device");
        deviceService.delete(clientInfo, deleteInput);
    }

}
