package com.cocotalk.push.dto.common;
import com.cocotalk.push.dto.common.ClientType;
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
