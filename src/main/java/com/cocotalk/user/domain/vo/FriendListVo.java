package com.cocotalk.user.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendListVo {
    private UserVo friend;
    private int status; // 0 = 나만 추가하거나 둘다 추가한, 1 = 상대방만 추가한

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (!(obj instanceof FriendListVo)) return false;
        FriendListVo friendListVo = (FriendListVo) obj;
        return Objects.equals(friendListVo.getFriend().getId(), friend.getId());
    }
}
