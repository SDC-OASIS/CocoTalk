package com.cocotalk.chat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewRoomVo {
    private RoomVo roomVo;
    private BundleInfoVo bundleInfoVo;
}
