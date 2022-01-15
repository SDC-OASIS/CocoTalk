//package com.cocotalk.chat.repository;
//
//import com.cocotalk.chat.model.request.RoomRequest;
//import org.springframework.stereotype.Repository;
//
//import javax.annotation.PostConstruct;
//import java.util.*;
//
//@Repository
//public class ChatRoomRepository {
//
//    private Map<String, RoomRequest> chatRoomDtoMap;
//
//    @PostConstruct
//    private void init(){
//        chatRoomDtoMap = new LinkedHashMap<>();
//    }
//
//    public List<RoomRequest> findAllRooms(){
//        //채팅방 생성 순서 최근 순으로 반환
//        List<RoomRequest> result = new ArrayList<>(chatRoomDtoMap.values());
//        Collections.reverse(result);
//
//        return result;
//    }
//
//    public RoomRequest findRoomById(String id){
//        return chatRoomDtoMap.get(id);
//    }
//
//    public RoomRequest createChatRoomDto(String name){
//        RoomRequest room = RoomRequest.create(name);
//        chatRoomDtoMap.put(room.getRoomId(), room);
//
//        return room;
//    }
//}
