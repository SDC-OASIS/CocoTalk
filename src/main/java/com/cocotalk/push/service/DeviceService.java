package com.cocotalk.push.service;

import com.cocotalk.push.dto.device.*;
import com.cocotalk.push.dto.common.ClientType;
import com.cocotalk.push.entity.Device;
import com.cocotalk.push.repository.DeviceRepository;
import com.cocotalk.push.support.PushException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.cocotalk.push.dto.common.response.ResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final ObjectMapper mapper;

    public Mono<Device> find(long id) {
        return deviceRepository.findById(id);
    }

    public Flux<Device> findByOptions(SelectInput selectInput) {
        if(selectInput.getType()==null)
            return deviceRepository.findByUserId(selectInput.getUserId());
        short type = (short)selectInput.getType().ordinal();
        return Flux.from(deviceRepository.findByUserIdAndType(selectInput.getUserId(), type));
    }

    public Mono<Device> create(ClientInfo clientInfo, CreateInput createInput) {
        log.info("client ip : "+ clientInfo);
        short clientType = parseClientType(clientInfo.getAgent());

        Mono<Device> output = deviceRepository.existsByUserIdAndType(createInput.getUserId(), clientType)
                .flatMap(res -> {
                    log.info("isExists:"+res.booleanValue());
                    if(res.booleanValue()==true)
                        return Mono.error(new PushException(EXISTS_INFO));
                    Device device = Device.builder()
                            .userId(createInput.getUserId())
                            .token(createInput.getFcmToken())
                            .ip(clientInfo.getIp())
                            .agent(clientInfo.getAgent())
                            .type(clientType)
                            .loggedinAt(LocalDateTime.now())
                            .build();
                    return deviceRepository.save(device)
                            .doOnError((e) -> {throw new PushException(DATABASE_ERROR, e);});
                });
        return output;
    }

    public Mono<Device> update(ClientInfo clientInfo, UpdateInput updateInput) {
        log.info("client ip : "+ clientInfo);
        short clientType = parseClientType(clientInfo.getAgent());
        Mono<Device> device = deviceRepository.findByUserIdAndType(updateInput.getUserId(), clientType)
                .flatMap(target -> {
                    target.setIp(clientInfo.getIp());
                    target.setAgent(clientInfo.getAgent());
                    target.setToken(updateInput.getFcmToken());
                    target.setLoggedinAt(LocalDateTime.now());
                    return deviceRepository.save(target);
                })
                .doOnError((e) -> {throw new PushException(DATABASE_ERROR, e);});
        return device;
    }

    public void delete(ClientInfo clientInfo, DeleteInput deleteInput) {
        log.info("client ip : "+ clientInfo);
        short clientType = parseClientType(clientInfo.getAgent());
        log.info("userId and type : "+deleteInput.getUserId()+","+clientType);
        deviceRepository.findByUserIdAndType(deleteInput.getUserId(), clientType)
                .doOnNext(target -> {
                    deviceRepository.delete(target).subscribe();
                })
                .doOnError((e) -> {throw new PushException(DATABASE_ERROR, e);})
                .subscribe();
    }

    private short parseClientType(String userAgent){
        if(userAgent.contains("Mozilla"))
            return (short) ClientType.MOBILE.ordinal();
        return (short) ClientType.WEB.ordinal();
    }

}
