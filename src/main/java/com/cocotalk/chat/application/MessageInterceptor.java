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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MessageInterceptor implements HandlerInterceptor {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final RoomMemberMapper roomMemberMapper;

    public static final CustomException INVALID_ROOM_ID =
            new CustomException(CustomError.BAD_REQUEST, "해당 roomId를 갖는 채팅방이 존재하지 않습니다.");

    public static final CustomException NOT_PERMITTED =
            new CustomException(CustomError.NOT_PERMITTED);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, Object> reqMap = getRequestParameterMap(request); // 쿼리 스트링 파싱

        String rid = (String)reqMap.get("roomId");

        if(rid != null) {
            TokenPayload payload = JwtUtil.getPayload(request.getHeader("X-ACCESS-TOKEN")); // Access Token 추출

            ObjectId roomId = new ObjectId(rid);

            RoomVo roomVo = roomMapper.toVo(roomRepository.findById(roomId).orElseThrow(() -> INVALID_ROOM_ID)); // 채팅방 정보 조회

            Long userId = payload.getUserId();

            RoomMemberVo roomMemberVo = roomVo.getMembers().stream() // 토큰에 포함된 userId가 채팅방 멤버로 참가중인지 확인
                    .filter(member -> userId.equals(member.getUserId()))
                    .findFirst()
                    .map(roomMemberMapper::toVo)
                    .orElseThrow(() -> NOT_PERMITTED);

            request.setAttribute("roomVo", roomVo); // 조회한 채팅방과 채팅방 멤버 정보 request에 set
            request.setAttribute("roomMemberVo", roomMemberVo);
        }
        return true;
    }

    public static Map<String, Object> getRequestParameterMap(HttpServletRequest req) {
        Map<String, Object> reqParamMaps = new HashMap<>();
        Map<String, Object> reqMap = new HashMap<>();

        reqParamMaps.putAll(req.getParameterMap());
        Set<?> keySet = reqParamMaps.keySet();

        for (Object key : keySet) {
            Object obj = reqParamMaps.get(key);
            if (obj.getClass().isArray() || Array.getLength(obj) == 1) {
                reqMap.put(key.toString(), Array.get(obj, 0));
            } else {
                reqMap.put(key.toString(), obj);
            }
        }
        return reqMap;
    }
}
