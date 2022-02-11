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
public class FriendInfoVo {
    private UserVo friend;
    private int status; // 0 = 나만 추가하거나 둘다 추가한, 1 = 상대방만 추가한, 2 = 내가 추가하지 않은, 3 = 자기 자신

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (!(obj instanceof FriendInfoVo)) return false;
        FriendInfoVo friendInfoVo = (FriendInfoVo) obj;
        return Objects.equals(friendInfoVo.getFriend().getId(), friend.getId());
    }
}
