package com.cocotalk.push.controller;

import com.cocotalk.push.dto.device.*;
import com.cocotalk.push.entity.Device;
import com.cocotalk.push.service.DeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.Update;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@Api(tags = "디바이스 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceService deviceService;

    @ApiOperation(value = "pk로 device 찾기")
    @GetMapping("/{id}")
    public Mono<Device> find(@PathVariable long id) {
        log.info("[device/post] find device");
        return deviceService.find(id);
    }

    @ApiOperation(value = "(userId) 혹은 (userId,type)으로 device 정보 찾기")
    @GetMapping
    public Flux<Device> findByUserId(@Valid SelectInput selectInput) {
        log.info("[device/post] findByUserId device");
        return deviceService.findByOptions(selectInput);
    }

    @ApiOperation(value = "device 등록하기")
    @PostMapping
    public Mono<Device> create(ClientInfo clientInfo, @RequestBody @Valid CreateInput createInput) {
        log.info("[device/post] create device");
        return deviceService.create(clientInfo, createInput);
    }
    
    @ApiOperation(value = "device 수정하기")
    @PutMapping
    public Mono<Device> update(ClientInfo clientInfo, @RequestBody @Valid UpdateInput updateInput) {
        log.info("[device/put] update device");
        return deviceService.update(clientInfo, updateInput);
    }

    @ApiOperation(value = "device 삭제하기")
    @DeleteMapping
    public void delete(ClientInfo clientInfo, @Valid DeleteInput deleteInput) {
        log.info("[device/delete] delete device");
        deviceService.delete(clientInfo, deleteInput);
    }

}
