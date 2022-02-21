package com.cocotalk.user.controller;

import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.domain.vo.FriendInfoVo;
import com.cocotalk.user.domain.vo.FriendVo;
import com.cocotalk.user.domain.vo.UserVo;
import com.cocotalk.user.dto.request.FriendAddRequest;
import com.cocotalk.user.dto.request.FriendHideRequest;
import com.cocotalk.user.dto.request.FriendsAddRequest;
import com.cocotalk.user.dto.response.CustomResponse;
import com.cocotalk.user.exception.CustomException;
import com.cocotalk.user.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
     * 친구 추가 API [POST] /friends
     * @param fromUser 친구 추가를 요청한 유저의 정보
     * @param friendAddRequest 친구로 추가되는 유저 id를 포함한 요청 모델
     * @return ResponseEntity<CustomResponse<FriendVo>> 추가된 친구 정보가 데이터에 포함됩니다.
     * @exception CustomException 해당 id를 가진 친구가 존재하지 않거나 이미 존재하는 친구라면 BAD_REQUEST 예외가 발생합니다.
     */
    @Operation(summary = "친구 추가")
    @PostMapping
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<FriendVo>> add(
            @Parameter(hidden = true) User fromUser,
            @RequestBody @Valid FriendAddRequest friendAddRequest) {
        FriendVo data = friendService.add(fromUser, friendAddRequest);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * userId 리스트로 친구 여러명 추가 API [POST] /friends
     * @param fromUser 친구 추가를 요청한 유저의 정보
     * @param friendsAddRequest 친구로 추가되는 유저 id를 포함한 요청 모델
     * @return ResponseEntity<CustomResponse<FriendVo>> 추가된 친구 정보가 데이터에 포함됩니다.
     */
    @Operation(summary = "userId 리스트로 친구 여러명 추가")
    @PostMapping("/list")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<List<FriendVo>>> addList(
            @Parameter(hidden = true) User fromUser,
            @RequestBody @Valid FriendsAddRequest friendsAddRequest) {
        List<FriendVo> data = friendService.addList(fromUser, friendsAddRequest);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * 친구 전체 조회 API [GET] /friends
     *
     * @param fromUser 친구 조회를 요청한 유저의 정보
     * @return ResponseEntity<CustomResponse<List<FriendInfoVo>> 자신의 친구 정보(자신을 친구로 추가한 사람 포함)가 리스트 데이터에 포함됩니다.
     */
    @Operation(summary = "친구 전체 조회")
    @GetMapping
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<List<FriendInfoVo>>> find(@Parameter(hidden = true) User fromUser) {
        List<FriendInfoVo> data = friendService.findAll(fromUser);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * 코코톡 Id(cid)으로 친구 조회 API [GET] /friends/cid/{cocotalkId}
     *
     * @param fromUser 친구 조회를 요청한 유저의 정보
     * @return ResponseEntity<CustomResponse<List<FriendInfoVo>> 코코톡 Id로 찾은 친구 정보가  data에 포함됩니다.
     */
    @Operation(summary = "코코톡 Id(cid)으로 친구 조회")
    @GetMapping("/cid/{cocotalkId}")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<FriendInfoVo>> findByCid(@Parameter(hidden = true) User fromUser,
                                                                  @PathVariable String cocotalkId) {
        FriendInfoVo data = friendService.findByCid(fromUser, cocotalkId);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * 숨긴 친구 조회 API [GET] /friends/hidden
     *
     * @param fromUser 숨긴 친구를 조회하는 유저
     * @return ResponseEntity<CustomResponse<List<UserVo>>> 숨겨진 친구들의 정보가 data에 포함됩니다.
     */
    @Operation(summary = "숨긴 친구 조회")
    @GetMapping("/hidden")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<List<UserVo>>> findHiddenFriends(@Parameter(hidden = true) User fromUser) {
        List<UserVo> data = friendService.findHiddenFriends(fromUser);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * 친구 숨김 수정 API [PATCH] /friends/hidden
     *
     * @param fromUser 친구 숨김을 요청한 유저의 정보
     * @param request 친구 숨김 요청 모델
     * @return ResponseEntity<CustomResponse<FriendVo>> 자신의 userId와 숨겨진 친구 userId,숨김 여부가 data에 포함됩니다.
     * @exception CustomException 해당 id를 가진 친구가 존재하지 않는다면 BAD_REQUEST 예외가 발생합니다.
     */
    @Operation(summary = "친구 숨김 수정")
    @PatchMapping("/hidden")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<FriendVo>> find(@Parameter(hidden = true) User fromUser,
                                                         @RequestBody FriendHideRequest request) {
        FriendVo data = friendService.hide(fromUser, request);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * 친구 삭제 API [DELETE] /friends/{id}
     *
     * @param fromUser 친구 삭제를 요청한 유저의 정보
     * @param id 삭제할 친구의 userId
     * @return ResponseEntity<CustomResponse<String>> 삭제된 유저 cid를 포함한 메시지가 data에 포함됩니다.
     * @exception CustomException 해당 id를 가진 친구가 존재하지 않는다면 BAD_REQUEST 예외가 발생합니다.
     */
    @Operation(summary = "친구 삭제")
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<?>> delete(@Parameter(hidden = true) User fromUser, @PathVariable Long id) {
        String result = friendService.delete(fromUser, id);
        return new ResponseEntity<>(new CustomResponse<>(result), HttpStatus.OK);
    }
}