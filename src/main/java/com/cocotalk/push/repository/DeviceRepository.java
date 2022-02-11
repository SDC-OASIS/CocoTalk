package com.cocotalk.push.repository;

import com.cocotalk.push.entity.Device;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DeviceRepository extends R2dbcRepository<Device, Long> {

    Flux<Device> findByUserId(long userId);
    Mono<Device> findByUserIdAndType(long userId, short type);
    Flux<Device> findByUserIdIn(List<Long> userIdList);

}

