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
        Map<?, ?> pathVariables = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String id = (String) pathVariables.get("id");

        if(id != null) {
            TokenPayload payload = JwtUtil.getPayload(request.getHeader("X-ACCESS-TOKEN"));

            ObjectId roomId = new ObjectId(id);

            RoomVo roomVo = roomMapper.toVo(roomRepository.findById(roomId).orElseThrow(() -> INVALID_ROOM_ID));

            Long userId = payload.getUserId();

            RoomMemberVo roomMemberVo = roomVo.getMembers().stream()
                    .filter(member -> userId.equals(member.getUserId()))
                    .findFirst()
                    .map(roomMemberMapper::toVo)
                    .orElseThrow(() -> NOT_PERMITTED);

            request.setAttribute("roomVo", roomVo);
            request.setAttribute("roomMemberVo", roomMemberVo);
        }
        return true;
    }
}
