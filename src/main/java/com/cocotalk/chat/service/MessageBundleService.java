package com.cocotalk.chat.service;

import com.cocotalk.chat.domain.entity.message.MessageBundle;
import com.cocotalk.chat.domain.vo.ChatMessageVo;
import com.cocotalk.chat.domain.vo.MessageBundleVo;
import com.cocotalk.chat.domain.vo.MessageSaveVo;
import com.cocotalk.chat.domain.vo.RoomVo;
import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import com.cocotalk.chat.repository.MessageBundleRepository;
import com.cocotalk.chat.utils.mapper.MessageBundleMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageBundleService {
    private final MessageBundleRepository messageBundleRepository;
    private final MessageBundleMapper messageBundleMapper;

    public static final List<ObjectId> emptyObjectIdList = new ArrayList<>();

    public static final CustomException INVALID_MESSAGE_BUNDLE_ID =
            new CustomException(CustomError.BAD_REQUEST, "해당 messageBundleId를 갖는 메시지 번들이 존재하지 않습니다.");

    public MessageBundleVo create(ObjectId roomId) {
        MessageBundle messageBundle = MessageBundle.builder()
                .roomId(roomId)
                .count(0)
                .messageIds(emptyObjectIdList)
                .build();
        return messageBundleMapper.toVo(messageBundleRepository.save(messageBundle));
    }

    public MessageBundleVo find(ObjectId id) {
        MessageBundle messageBundle = messageBundleRepository.findById(id)
                .orElseThrow(() -> INVALID_MESSAGE_BUNDLE_ID);
        return messageBundleMapper.toVo(messageBundle);
    }

    public MessageSaveVo findRecentMessageBundle(RoomVo roomVo) {
        List<ObjectId> messageBundleIds = roomVo.getMessageBundleIds();
        MessageBundleVo recentMessageBundleVo;
        if(messageBundleIds.isEmpty()) {
            recentMessageBundleVo = this.create(roomVo.getId()); // 번들 생성, 저장
            messageBundleIds.add(recentMessageBundleVo.getId()); // 생성된 번들 id, roomVo에 저장
        } else {
            ObjectId messageBundleId = messageBundleIds.get(messageBundleIds.size() - 1);
            MessageBundleVo oldMessageBundleVo = this.find(messageBundleId);
            if(oldMessageBundleVo.getCount() < 10)
                recentMessageBundleVo = oldMessageBundleVo;
            else {
                recentMessageBundleVo = this.create(roomVo.getId());
                messageBundleIds.add(recentMessageBundleVo.getId());
            }
        }
        return new MessageSaveVo(roomVo, recentMessageBundleVo);
    }

//    public MessageBundleVo saveMessageBundle(ObjectId roomId, ChatMessageVo chatMessageVo) {
//        RoomVo roomVo = roomService.findById(roomId); // (1) roomVo 찾고
//        List<ObjectId> messageBundleIds = roomVo.getMessageBundleIds(); // (2) 그 안에 있는 messageBundleIds 가져오고
//        MessageBundleVo messageBundleVo;
//        if(messageBundleIds.isEmpty()) { // (3) messageBundleId가 하나도 없다면
//            MessageBundleVo newMessageBundleVo = this.create(roomId);
//            // (4) MessageBundle 하나 만들어서 MessageId 저장
//            messageBundleVo = this.saveMessageId(newMessageBundleVo, chatMessageVo);
//            messageBundleIds.add(messageBundleVo.getId());
//        } else { // (5) 메시지 번들이 하나라도 있다면
//            ObjectId messageBundleId = messageBundleIds.get(messageBundleIds.size() - 1); // (6) room의 messageBundleIds에서 마지막 메시지 번들 ID 찾아주고
//            MessageBundleVo oldMessageBundleVo = this.find(messageBundleId);
//            if(oldMessageBundleVo.getCount() <= 10) // (7) 기존 메시지 번들에 여유가 있다면 그대로 저장
//                messageBundleVo = this.saveMessageId(oldMessageBundleVo, chatMessageVo);
//            else { // (8) 기존 메시지 번들에 slicing 기준만큼 메시지가 쌓였으면
//                MessageBundleVo newMessageBundleVo = this.create(roomId); // (9) 새 메시지 번들 생성하여 저장
//                messageBundleVo = this.saveMessageId(newMessageBundleVo, chatMessageVo);
//                messageBundleIds.add(messageBundleVo.getId());
//            }
//        }
//        roomService.save(roomVo);
//        return messageBundleVo;
//    }

    public MessageBundleVo saveMessageId(MessageBundleVo messageBundleVo, ChatMessageVo chatMessageVo) {
        messageBundleVo.getMessageIds().add(chatMessageVo.getId());
        MessageBundle messageBundle = messageBundleMapper.toEntity(messageBundleVo);
        messageBundle.setCount(messageBundleVo.getCount() + 1);
        return messageBundleMapper.toVo(messageBundleRepository.save(messageBundle));
    }
}
