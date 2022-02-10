package com.cocotalk.user.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendVo {
    private Long id;
    private UserVo fromUser;
    private UserVo toUser;
    private boolean hidden;
}
