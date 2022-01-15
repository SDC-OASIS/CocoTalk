package com.cocotalk.chat.controller;

import com.cocotalk.chat.document.Room;
import com.cocotalk.chat.model.request.RoomRequest;
import com.cocotalk.chat.model.response.GlobalResponse;
import com.cocotalk.chat.model.response.RoomResponse;
import com.cocotalk.chat.service.RoomService;
import com.cocotalk.chat.utils.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/chat/room")
public class RoomController {
    private final RoomService roomService;
    private final RoomMapper roomMapper;

    @GetMapping("/private")
    public ResponseEntity<?> findPrivateRoom(@RequestParam String me, @RequestParam String friend){
        Optional<Room> roomOptional = roomService.findPrivateRoom(me, friend);
        RoomResponse data = roomOptional.map(roomMapper::toDto).orElse(null);
        return new ResponseEntity<>(new GlobalResponse(data), HttpStatus.OK);
    }

    @PostMapping("/private")
    public ResponseEntity<?> createPrivateRoom(@RequestBody RoomRequest request){
        Room privateRoom = roomService.createPrivateRoom(roomMapper.toEntity(request));
        RoomResponse data = roomMapper.toDto(privateRoom);
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.CREATED);
    }
}
