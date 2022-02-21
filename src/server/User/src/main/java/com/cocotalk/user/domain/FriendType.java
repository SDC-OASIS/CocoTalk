package com.cocotalk.user.domain;

public enum FriendType {
    ACTIVE, // 0 = 나만 추가하거나 둘다 추가한
    PASSIVE, //  1 = 상대방만 추가한
    NOT_ADDED, // 2 = 내가 추가하지 않은
    YOURSELF; // 3 = 자기 자신
}
