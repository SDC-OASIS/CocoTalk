package com.cocotalk.push.repository;

import com.cocotalk.push.entity.Device;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface DeviceRepository extends R2dbcRepository<Device, Long> {


}

