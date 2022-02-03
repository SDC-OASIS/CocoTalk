package com.cocotalk.push.controller;

import com.cocotalk.push.dto.device.*;
import com.cocotalk.push.entity.Device;
import com.cocotalk.push.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.Update;
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

    @Operation(summary  = "pk로 device 찾기")
    @GetMapping("/{id}")
    public Mono<Device> find(@PathVariable long id) {
        log.info("[device/post] find device");
        return deviceService.find(id);
    }

    @Operation(summary = "(userId) 혹은 (userId,type)으로 device 정보 찾기")
    @GetMapping
    public Flux<Device> findByUserId(@Valid SelectInput selectInput) {
        log.info("[device/post] findByUserId device");
        return deviceService.findByOptions(selectInput);
    }

    @Operation(summary = "device 등록하기")
    @PostMapping
    public Mono<Device> create(ClientInfo clientInfo, @RequestBody @Valid CreateInput createInput) {
        log.info("[device/post] create device");
        return deviceService.create(clientInfo, createInput);
    }
    
    @Operation(summary = "device 수정하기")
    @PutMapping
    public Mono<Device> update(ClientInfo clientInfo, @RequestBody @Valid UpdateInput updateInput) {
        log.info("[device/put] update device");
        return deviceService.update(clientInfo, updateInput);
    }

    @Operation(summary = "device 삭제하기")
    @DeleteMapping
    public void delete(ClientInfo clientInfo, @Valid DeleteInput deleteInput) {
        log.info("[device/delete] delete device");
        deviceService.delete(clientInfo, deleteInput);
    }

}
