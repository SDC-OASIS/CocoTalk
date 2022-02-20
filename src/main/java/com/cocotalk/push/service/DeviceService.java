package com.cocotalk.push.service;

import com.cocotalk.push.dto.common.ClientInfo;
import com.cocotalk.push.dto.device.*;
import com.cocotalk.push.dto.common.ClientType;
import com.cocotalk.push.entity.Device;
import com.cocotalk.push.repository.DeviceRepository;
import com.cocotalk.push.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.cocotalk.push.dto.common.response.ResponseStatus.*;

/**
 *
 * Device를 관리하는 메서드들이 포함된 클래스 입니다.
 * 주로 Device의 FCM TOKEN을 관리하기 위해 사용됩니다.
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;

    /**
     * device id로 device 정보를 찾습니다
     *
     * @param id device pk
     * @return 조회한 device 정보
     */
    public Mono<Device> find(long id) {
        return deviceRepository.findById(id);
    }

    /**
     * (userId) or (userId and Type)의 조건으로 device 정보들을 찾습니다.
     *
     * @param selectInput device를 조회할 조건이 담긴 요청 모델
     * @return 조회한 device들의 정보
     */
    public Flux<Device> findByOptions(SelectInput selectInput) {
        if(selectInput.getType()==null)
            return deviceRepository.findByUserId(selectInput.getUserId());
        short type = (short) selectInput.getType().ordinal();
        return Flux.from(deviceRepository.findByUserIdAndType(selectInput.getUserId(), type));
    }

    /**
     * client Type(MOBILE/WEB)과 userId가 일치하는 fcmToken을 갱신합니다.
     * 
     * @param clientInfo 요청자의 client 정보 ( MOBILE/WEB을 별도로 관리하기 위해 필요 )
     * @param saveInput 갱신될 userId와  갱신할 fcmToken이 담긴 요청 모델
     * @return 갱신된 device 정보
     */
    public Mono<Device> save(ClientInfo clientInfo, SaveInput saveInput) {
        log.info("[save/SaveInput] : " + saveInput);
        log.info("[save/clientInfo] : " + clientInfo);
        short clientType = (short) clientInfo.getClientType().ordinal();
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
                ).doOnError((e) -> {throw new CustomException(DATABASE_ERROR, e);});
    }

    /**
     * client Type(MOBILE/WEB)과 userId가 일치하는 device 정보를 삭제합니다.
     *
     * @param clientInfo 요청자의 client 정보 ( MOBILE/WEB을 별도로 관리하기 위해 필요 )
     * @param deleteInput 삭제할 userId
     */
    public void delete(ClientInfo clientInfo, DeleteInput deleteInput) {
        short clientType = (short) clientInfo.getClientType().ordinal();
        log.info("userId and type : "+deleteInput.getUserId()+","+clientType);
        deviceRepository.findByUserIdAndType(deleteInput.getUserId(), clientType)
                .doOnNext(target -> {
                    deviceRepository.delete(target).subscribe();
                })
                .doOnError((e) -> {throw new CustomException(DATABASE_ERROR, e);})
                .subscribe();
    }

}
