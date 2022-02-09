package com.cocotalk.auth.dto.common;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class ClientInfo {
    String ip;
    String agent;
    ClientType clientType;
}
