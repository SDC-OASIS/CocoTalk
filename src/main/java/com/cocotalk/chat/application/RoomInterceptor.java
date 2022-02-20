package com.cocotalk.chat.application;

import com.cocotalk.chat.domain.vo.RoomMemberVo;
import com.cocotalk.chat.domain.vo.RoomVo;
import com.cocotalk.chat.dto.TokenPayload;
import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import com.cocotalk.chat.repository.RoomRepository;
import com.cocotalk.chat.utils.JwtUtil;
import com.cocotalk.chat.utils.mapper.RoomMapper;
import com.cocotalk.chat.utils.mapper.RoomMemberMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RoomInterceptor implements HandlerInterceptor {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final RoomMemberMapper roomMemberMapper;

    public static final CustomException INVALID_ROOM_ID =
            new CustomException(CustomError.BAD_REQUEST, "해당 roomId를 갖는 채팅방이 존재하지 않습니다.");

    public static final CustomException NOT_PERMITTED =
            new CustomException(CustomError.NOT_PERMITTED);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // pathVariable 추출
        Map<?, ?> pathVariables = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String id = (String) pathVariables.get("id");

        if(id != null) {
            TokenPayload payload = JwtUtil.getPayload(request.getHeader("X-ACCESS-TOKEN")); // Access Token 추출

            ObjectId roomId = new ObjectId(id);

            RoomVo roomVo = roomMapper.toVo(roomRepository.findById(roomId).orElseThrow(() -> INVALID_ROOM_ID)); // 채팅방 정보 조회

            Long userId = payload.getUserId();

            RoomMemberVo roomMemberVo = roomVo.getMembers().stream()// 토큰에 포함된 userId가 채팅방 멤버로 참가중인지 확인
                    .filter(member -> userId.equals(member.getUserId()))
                    .findFirst()
                    .map(roomMemberMapper::toVo)
                    .orElseThrow(() -> NOT_PERMITTED);

            request.setAttribute("roomVo", roomVo); // 조회한 채팅방과 채팅방 멤버 정보 request에 set
            request.setAttribute("roomMemberVo", roomMemberVo);
        }
        return true;
    }
}
