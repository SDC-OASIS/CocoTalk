package com.cocotalk.push.controller;

import com.cocotalk.push.entity.Device;
import com.cocotalk.push.repository.DeviceRepository;
import com.cocotalk.push.response.Response;
import com.cocotalk.push.response.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.cocotalk.push.response.ResponseStatus.*;

@Slf4j
@RestController
@RequestMapping("/api/device")
@AllArgsConstructor
public class DeviceController {

    private  final DeviceRepository deviceRepository;

    @GetMapping("/{id}")
    public Mono<Device> find(@PathVariable long id) {
        log.info("[device/post] find device");
        return deviceRepository.findById(id);
    }

    @GetMapping
    public Flux<Device> findAll() {
        log.info("[device/post] findAll device");
        return deviceRepository.findAll();
    }

    @PostMapping
    public Mono<Device> create() {
        log.info("[device/post] create device");
        try{
            Device device = Device.builder()
                    .userId((long)1)
                    .token("token")
                    .ip("0.0.0.0")
                    .os("Windows")
                    .type((short)1)
                    .build();
//            Mono<Device> output = deviceRepository.save(device);
//            return output;
            return null;
        }catch(Exception e){
            log.error("[device/post] database error",e);
            return null;
//            return Mono.just(ResponseEntity.status(HttpStatus.OK).body(new Response<>(DATABASE_ERROR)));
        }
    }

}
