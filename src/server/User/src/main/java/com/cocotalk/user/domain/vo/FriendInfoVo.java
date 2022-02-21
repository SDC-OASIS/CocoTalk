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
    private UserVo friend; // 친구의 상세 정보
    private int type;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (!(obj instanceof FriendInfoVo)) return false;
        FriendInfoVo friendInfoVo = (FriendInfoVo) obj;
        return Objects.equals(friendInfoVo.getFriend().getId(), friend.getId());
    }
}
