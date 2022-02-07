package com.cocotalk.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {
    @NotBlank(message = "방 이름은 필수값 입니다.")
    private String roomName;

    private String img;

    private Short type;

    private List<RoomMemberRequest> members;
}