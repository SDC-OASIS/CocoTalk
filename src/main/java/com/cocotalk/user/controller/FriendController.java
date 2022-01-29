package com.cocotalk.user.controller;

import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.domain.vo.FriendVo;
import com.cocotalk.user.domain.vo.UserVo;
import com.cocotalk.user.dto.request.FriendAddRequest;
import com.cocotalk.user.dto.response.CustomResponse;
import com.cocotalk.user.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * 친구 관련 API 요청을 받는 컨트롤러
 *
 * @author ybell1028
 * @version 1.0, 최초 작성
 * @see com.cocotalk.user.service.FriendService
 */

@Slf4j
@Tag(name = "친구 API")
@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    /**
     * 친구 추가 API [POST] /api/user/friend
     * @param fromUser 친구 추가를 요청하는 유저
     * @param friendAddRequest 친구로 추가되는 유저 id를 포함한 요청 모델
     * @return ResponseEntity<CustomResponse<FriendResponse>> 추가된 친구 정보가 데이터에 포함됩니다.
     * @exception
     */
    @Operation(summary = "친구 추가")
    @PostMapping
    public ResponseEntity<CustomResponse<FriendVo>> add(
            User fromUser,
            @RequestBody @Valid FriendAddRequest friendAddRequest) {
        FriendVo data = friendService.add(fromUser, friendAddRequest);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * 친구 조회 API [GET] /api/user/friend
     *
     * @param fromUser 친구 조회를 요청하는 유저
     * @return ResponseEntity<CustomResponse<UserVo>> 자신의 친구 정보 리스트 데이터에 포함됩니다.
     */
    @Operation(summary = "친구 조회")
    @GetMapping
    public ResponseEntity<CustomResponse<List<UserVo>>> find(User fromUser) {
        List<UserVo> data = friendService.find(fromUser);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * 친구 삭제 API [DELETE] /api/user/friend/{id}
     *
     * @param fromUser 친구 삭제를 요청하는 유저
     * @param id 삭제할 친구의 userId
     * @return ResponseEntity<CustomResponse<String>> 삭제된 유저 cid를 포함한 메시지가 포함됩니다.
     */
    @Operation(summary = "친구 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<?>> delete(User fromUser, @PathVariable Long id) {
        String result = friendService.delete(fromUser, id);
        return new ResponseEntity<>(new CustomResponse<>(), HttpStatus.OK);
    }
}