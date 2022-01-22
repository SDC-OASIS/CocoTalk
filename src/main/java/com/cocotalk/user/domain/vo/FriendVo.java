package com.cocotalk.user.domain.vo;


import com.cocotalk.user.domain.entity.User;
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
    private User fromUser;
    private User toUser;
}
