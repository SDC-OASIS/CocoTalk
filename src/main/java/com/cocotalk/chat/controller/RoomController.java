package com.cocotalk.chat.controller;

import com.cocotalk.chat.domain.vo.*;
import com.cocotalk.chat.dto.request.RoomRequest;
import com.cocotalk.chat.dto.response.CustomResponse;
import com.cocotalk.chat.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "채팅방 API")
@RequiredArgsConstructor
@RequestMapping(value = "/rooms")
public class RoomController {
    private final RoomService roomService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 채팅방 생성 API [POST] /rooms
     *
     * @param userVo 요청한 유저의 정보
     * @param request 새로운 채팅방 생성을 요청하는 모델
     * @return ResponseEntity<CustomResponse<RoomVo>> 생성된 채팅방 정보가 포함됩니다.
     * @exception com.cocotalk.chat.exception.CustomException 채팅방 멤버수가 2보다 작으면 BAD_REQUEST 예외가 발생합니다.
     */
    @PostMapping
    @Operation(summary = "채팅방 생성")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<RoomVo>> create(@Parameter(hidden = true) UserVo userVo,
                                                    @RequestBody @Valid RoomRequest request){
        RoomVo data = roomService.create(request);
        int size = data.getMembers().size();
        for(int i = 0; i < size; ++i) {
            long userId = data.getMembers().get(i).getUserId();
            simpMessagingTemplate.convertAndSend("/topic/" + userId + "/room/new", data);
        }
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.CREATED);
    }


    /**
     * 채팅방 조회 API [GET] /rooms
     *
     * @param userVo 채팅방 생성을 요청한 유저의 정보
     * @return ResponseEntity<CustomResponse<RoomVo>> 자신이 참가중인 채팅방 상세 정보가 포함됩니다.
     */
    @GetMapping
    @Operation(summary = "유저가 포함된 모든 채팅방 상세 정보 조회 (참가하고 있지 않은 방 포함)")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<?>> findAll(@Parameter(hidden = true) UserVo userVo){
        List<RoomVo> data = new ArrayList<>(roomService.findAllJoining(userVo));
        return new ResponseEntity<>(new CustomResponse(data), HttpStatus.OK);
    }

    /**
     * 채팅방 리스트 조회 API [GET] /rooms/list
     *
     * @param userVo 채팅방 생성을 요청한 유저의 정보
     * @return ResponseEntity<CustomResponse<List<RoomListVo>>> 자신이 참가중인 채팅방 정보, 가장 최근 수신한 메시지, 읽지 않은 메시지 수가 포함됩니다.
     */
    @GetMapping("/list")
    @Operation(summary = "유저가 포함된 모든 채팅방 리스트 조회")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<List<RoomListVo>>> findRoomList(@Parameter(hidden = true) UserVo userVo){
        List<RoomListVo> data = new ArrayList<>(roomService.findRoomList(userVo));
        return new ResponseEntity<>(new CustomResponse(data), HttpStatus.OK);
    }

    /**
     * 채팅방 id로 조회 API [GET] /rooms/{id}
     *
     * @param id 채팅방의 ObjectId
     * @return ResponseEntity<CustomResponse<RoomVo>> 조회한 채팅방의 상세 정보가 포함됩니다.
     */
    @GetMapping("/{id}")
    @Operation(summary = "채팅방 id로 조회")
    public ResponseEntity<CustomResponse<RoomVo>> findById(@PathVariable ObjectId id){
        RoomVo data = roomService.findById(id);
        return new ResponseEntity<>(new CustomResponse(data), HttpStatus.OK);
    }

    /**
     * 채팅방 id과 messageBundleCount로 채팅방과 첫 메시지 페이지 조회 API [GET] /rooms/{id}/tail
     *
     * @param id 채팅방의 ObjectId
     * @param count 최신 메시지 번들에 쌓인 메시지 ObjectId의 수
     * @return ResponseEntity<CustomResponse<RoomVo>> 조회한 채팅방과 첫 메시지 페이지가 포함됩니다.
     */
    @GetMapping("/{id}/tail")
    @Operation(summary = "채팅방 id과 messageBundleCount로 채팅방과 첫 메시지 페이지 조회")
    public ResponseEntity<CustomResponse<RoomWithMessageListVo<ChatMessageVo>>> findRoomAndMessages(@PathVariable ObjectId id,
                                                                 @RequestParam int count) {
        RoomWithMessageListVo<ChatMessageVo> data = roomService.findRoomAndMessage(id, count);
        return new ResponseEntity<>(new CustomResponse(data), HttpStatus.OK);
    }

    /**
     * 상대방 userId로 1:1 채팅방 조회 API [GET] /rooms/private/{userId}
     *
     * @param userVo 요청한 유저의 정보
     * @param userId 1:1 채팅 상대방의 userId
     * @return ResponseEntity<CustomResponse<RoomVo>> 조회한 채팅방 정보가 포함됩니다.
     */
    @GetMapping("/private/{userId}")
    @Operation(summary = "내 정보와 다른 userId로 개인 채팅방 조회")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<RoomVo>> findPrivate(@Parameter(hidden = true) UserVo userVo, @PathVariable Long userId){
        RoomVo data = roomService.findPrivate(userVo, userId);
        return new ResponseEntity<>(new CustomResponse(data), HttpStatus.OK);
    }

    /**
     * 채팅방 수정 API [PUT] /rooms/{id}
     *
     * @param id 채팅방의 ObjectId
     * @return ResponseEntity<CustomResponse<RoomVo>> 수정한 채팅방의 상세 정보가 포함됩니다.
     */
    @PutMapping("/{id}")
    @Operation(summary = "채팅방 id로 수정")
    public ResponseEntity<CustomResponse<RoomVo>> modify(@PathVariable ObjectId id, @RequestBody @Valid RoomRequest request){
        RoomVo data = roomService.modify(id, request);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.CREATED);
    }

    /**
     * 채팅방 삭제 API [DELETE] /rooms/{id}
     *
     * @param id 채팅방의 ObjectId
     * @return ResponseEntity<CustomResponse<String>> 삭제된 채팅방의 이름과 메시지가 포함됩니다.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "채팅방 id로 삭제")
    public ResponseEntity<CustomResponse<String>> modify(@PathVariable ObjectId id){
        String data = roomService.deleteById(id);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.CREATED);
    }
}