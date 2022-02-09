package com.cocotalk.user.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class FriendsAddRequest {
    List<Long> toUidList;
}
