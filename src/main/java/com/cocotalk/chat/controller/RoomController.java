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

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/chat/rooms")
public class RoomController {
    private final RoomService roomService;
    private final RoomMapper roomMapper;

    @PostMapping
    public ResponseEntity<GlobalResponse<?>> create(@RequestBody @Valid RoomRequest request){
        Room room = roomService.save(roomMapper.toEntity(request));
        RoomResponse data = roomMapper.toDto(room);
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponse<?>> find(@PathVariable String id){
        Room room = roomService.find(id);
        RoomResponse data = roomMapper.toDto(room);
        return new ResponseEntity<>(new GlobalResponse(data), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<GlobalResponse<?>> findAll(@PathVariable Long userId){
        List<RoomResponse> data = roomService.findAll(userId).stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new GlobalResponse(data), HttpStatus.OK);
    }

    @GetMapping("/private")
    public ResponseEntity<GlobalResponse<?>> findPrivate(@RequestParam Long userid, @RequestParam Long friendid){
        Room room = roomService.findPrivate(userid, friendid);
        RoomResponse data = roomMapper.toDto(room);
        return new ResponseEntity<>(new GlobalResponse(data), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<GlobalResponse<?>> modify(@RequestBody RoomRequest request){
        Room room = roomService.save(roomMapper.toEntity(request));
        RoomResponse data = roomMapper.toDto(room);
        return new ResponseEntity<>(new GlobalResponse(data), HttpStatus.OK);
    }
}
