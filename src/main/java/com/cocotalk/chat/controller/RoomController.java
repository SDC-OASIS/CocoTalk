package com.cocotalk.chat.controller;

import com.cocotalk.chat.domain.vo.RoomListVo;
import com.cocotalk.chat.domain.vo.RoomVo;
import com.cocotalk.chat.domain.vo.UserVo;
import com.cocotalk.chat.dto.request.RoomRequest;
import com.cocotalk.chat.dto.response.GlobalResponse;
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

    @PostMapping
    @Operation(summary = "채팅방 생성")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<GlobalResponse<?>> create(@Parameter(hidden = true) UserVo userVo,
                                                    @RequestBody @Valid RoomRequest request){
        RoomVo data = roomService.create(request);
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.CREATED);
    }


    @GetMapping
    @Operation(summary = "유저가 포함된 모든 채팅방 상세 정보 조회 (참가하고 있지 않은 방 포함)")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<GlobalResponse<?>> findAll(@Parameter(hidden = true) UserVo userVo){
        List<RoomVo> data = new ArrayList<>(roomService.findAllJoining(userVo));
        return new ResponseEntity<>(new GlobalResponse(data), HttpStatus.OK);
    }

    @GetMapping("/list")
    @Operation(summary = "유저가 포함된 모든 채팅방 리스트 조회")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<GlobalResponse<?>> findRoomList(@Parameter(hidden = true) UserVo userVo){
        List<RoomListVo> data = new ArrayList<>(roomService.findRoomList(userVo));
        return new ResponseEntity<>(new GlobalResponse(data), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "채팅방 id로 조회")
    public ResponseEntity<GlobalResponse<?>> findById(@PathVariable ObjectId id){
        RoomVo data = roomService.findById(id);
        return new ResponseEntity<>(new GlobalResponse(data), HttpStatus.OK);
    }


    @GetMapping("/private/{userId}")
    @Operation(summary = "내 정보와 다른 userId로 개인 채팅방 조회")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<GlobalResponse<?>> findPrivate(@Parameter(hidden = true) UserVo userVo, @PathVariable Long userId){
        RoomVo data = roomService.findPrivate(userVo, userId);
        return new ResponseEntity<>(new GlobalResponse(data), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "채팅방 id로 수정")
    public ResponseEntity<GlobalResponse<?>> modify(@PathVariable ObjectId id, @RequestBody @Valid RoomRequest request){
        RoomVo data = roomService.modify(id, request);
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "채팅방 id로 삭제")
    public ResponseEntity<GlobalResponse<?>> modify(@PathVariable ObjectId id){
        String data = roomService.deleteById(id);
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.CREATED);
    }
}