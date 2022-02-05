package com.cocotalk.chat.service;


import com.cocotalk.chat.domain.entity.message.ChatMessage;
import com.cocotalk.chat.domain.entity.message.InviteMessage;
import com.cocotalk.chat.domain.vo.*;
import com.cocotalk.chat.dto.request.ChatMessageRequest;
import com.cocotalk.chat.dto.request.InviteMessageRequest;
import com.cocotalk.chat.repository.ChatMessageRepository;
import com.cocotalk.chat.utils.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final MessageBundleService messageBundleService;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageMapper messageMapper;

    @Value(value = "${cocotalk.message-bundle-limit}")
    private int messageBundleLimit;

    private static final ChatMessage emptyMessage = new ChatMessage();

    public ChatMessageVo find(ObjectId id) {
        return messageMapper.toVo(chatMessageRepository.findById(id).orElse(emptyMessage));
    }

    public MessageVo<ChatMessageVo> createChatMessage(ChatMessageRequest request) {
        ChatMessage chatMessage = messageMapper.toEntity(request);
        chatMessage.setSentAt(LocalDateTime.now());

        ChatMessageVo chatMessageVo = messageMapper.toVo(chatMessageRepository.save(chatMessage)); // (2) 메시지 저장
        MessageBundleVo oldMessageBundleVo = messageBundleService.find(request.getMessageBundleId()); // (3) 기존 메시지 번들 조회
        int count = messageBundleService.saveMessageId(oldMessageBundleVo, chatMessageVo).getCount(); // (4) 기존 메시지 번들에 메시지 Id 저장
        BundleInfoVo bundleInfoVo;
        if (count >= messageBundleLimit) {
            MessageBundleVo newMessageBundleVo = messageBundleService.create(request.getRoomId()); // (5) 카운트가 넘어갔다면 새로운 메시지 번들 생성
            bundleInfoVo = BundleInfoVo.builder()
                    .currentCount(count)
                    .currentMessageBundleId(oldMessageBundleVo.getId())
                    .nextMessageBundleId(newMessageBundleVo.getId())
                    .build();
        } else {
            bundleInfoVo = BundleInfoVo.builder()
                    .currentCount(count)
                    .currentMessageBundleId(oldMessageBundleVo.getId())
                    .nextMessageBundleId(oldMessageBundleVo.getId())
                    .build();
        }
        return new MessageVo<>(chatMessageVo, bundleInfoVo);
    }

    public MessageVo<InviteMessageVo> createInviteMessage(InviteMessageRequest request) {
        InviteMessage inviteMessage = messageMapper.toEntity(request);
        inviteMessage.setSentAt(LocalDateTime.now());

        InviteMessageVo inviteMessageVo = messageMapper.toVo(chatMessageRepository.save(inviteMessage));
        MessageBundleVo oldMessageBundleVo = messageBundleService.find(request.getMessageBundleId());
        int count = messageBundleService.saveMessageId(oldMessageBundleVo, inviteMessageVo).getCount();

        BundleInfoVo bundleInfoVo;
        if (count >= messageBundleLimit) {
            MessageBundleVo newMessageBundleVo = messageBundleService.create(request.getRoomId());
            bundleInfoVo = BundleInfoVo.builder()
                    .currentCount(count)
                    .currentMessageBundleId(oldMessageBundleVo.getId())
                    .nextMessageBundleId(newMessageBundleVo.getId())
                    .build();
        } else {
            bundleInfoVo = BundleInfoVo.builder()
                    .currentCount(count)
                    .currentMessageBundleId(oldMessageBundleVo.getId())
                    .nextMessageBundleId(oldMessageBundleVo.getId())
                    .build();
        }
        return new MessageVo<>(inviteMessageVo, bundleInfoVo);
    }

    public List<ChatMessageVo> findMessagePage(ObjectId roomId, ObjectId bundleId, int count, int size) {
        List<ObjectId> messageIds = new ArrayList<>();
        if(count >= size) {
            int start = count - size;
            messageIds.addAll(messageBundleService.findSlice(bundleId, start, size).getMessageIds()); ;
        } else {
            int diff = size - count; // count = 6, size = 10면 diff = 4
            int start = messageBundleLimit - diff;
            MessageBundleVo justBeforeBundle = messageBundleService.findJustBeforeAndSlice(roomId, bundleId, start, diff);
            if(justBeforeBundle.getMessageIds() != null) {
                messageIds.addAll(justBeforeBundle.getMessageIds());
            }
            messageIds.addAll(messageBundleService.findSlice(bundleId, 0, size - diff).getMessageIds());
        }
        return messageIds.stream().map(this::find).collect(Collectors.toList());
    }

    public ChatMessageVo modifyChatMessage(ChatMessageVo chatMessageVo) {
        ChatMessage chatMessage = messageMapper.toEntity(chatMessageVo);
        return messageMapper.toVo(chatMessageRepository.save(chatMessage));
    }

    public ChatMessageVo setMessageBundleId(ChatMessageVo chatMessageVo, MessageBundleVo messageBundleVo) {
        ChatMessage chatMessage = messageMapper.toEntity(chatMessageVo);
        chatMessage.setMessageBundleId(messageBundleVo.getId());
        return messageMapper.toVo(chatMessageRepository.save(chatMessage));
    }
}
