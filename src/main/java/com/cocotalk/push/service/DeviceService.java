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

    public Mono<Device> save(ClientInfo clientInfo, SaveInput saveInput) {
        log.info("[save/SaveInput] : " + saveInput);
        short clientType = parseClientType(clientInfo.getAgent());
        return deviceRepository.findByUserIdAndType(saveInput.getUserId(), clientType)
                .flatMap(target -> { // device 정보가 있는 경우
                    target.setToken(saveInput.getFcmToken());
                    target.setIp(clientInfo.getIp());
                    target.setAgent(clientInfo.getAgent());
                    target.setLoggedinAt(LocalDateTime.now());
                    return deviceRepository.save(target);
                }).switchIfEmpty( // device 정보가 없는 경우
                        deviceRepository.save(Device.builder()
                                .userId(saveInput.getUserId())
                                .token(saveInput.getFcmToken())
                                .ip(clientInfo.getIp())
                                .agent(clientInfo.getAgent())
                                .type(clientType)
                                .loggedinAt(LocalDateTime.now())
                                .build())
                ).doOnError((e) -> {throw new PushException(DATABASE_ERROR, e);});
    }

    public void delete(ClientInfo clientInfo, DeleteInput deleteInput) {
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
