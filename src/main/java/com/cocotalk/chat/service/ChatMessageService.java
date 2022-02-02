package com.cocotalk.chat.service;


import com.cocotalk.chat.domain.entity.message.ChatMessage;
import com.cocotalk.chat.domain.entity.message.InviteMessage;
import com.cocotalk.chat.domain.vo.ChatMessageVo;
import com.cocotalk.chat.domain.vo.InviteMessageVo;
import com.cocotalk.chat.domain.vo.MessageBundleVo;
import com.cocotalk.chat.dto.request.ChatMessageRequest;
import com.cocotalk.chat.dto.request.InviteMessageRequest;
import com.cocotalk.chat.repository.ChatMessageRepository;
import com.cocotalk.chat.utils.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final MessageBundleService messageBundleService;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageMapper messageMapper;

    private static final ChatMessage emptyMessage = new ChatMessage();

    public ChatMessageVo find(ObjectId id) {
        return messageMapper.toVo(chatMessageRepository.findById(id).orElse(emptyMessage));
    }

    public ChatMessageVo createChatMessage(ChatMessageRequest request, MessageBundleVo recentMessageBundleVo) {
        ChatMessage chatMessage = messageMapper.toEntity(request);
        chatMessage.setMessageBundleId(recentMessageBundleVo.getId());
        chatMessage.setSentAt(LocalDateTime.now());

        ChatMessageVo chatMessageVo = messageMapper.toVo(chatMessageRepository.save(chatMessage)); // 메시지 저장
        messageBundleService.saveMessageId(recentMessageBundleVo, chatMessageVo);

        return messageMapper.toVo(chatMessage);
    }

    public InviteMessageVo createInviteMessage(InviteMessageRequest request, MessageBundleVo recentMessageBundleVo) {
        InviteMessage inviteMessage = messageMapper.toEntity(request);
        inviteMessage.setMessageBundleId(recentMessageBundleVo.getId());
        inviteMessage.setSentAt(LocalDateTime.now());

        InviteMessageVo inviteMessageVo = messageMapper.toVo(chatMessageRepository.save(inviteMessage));
        messageBundleService.saveMessageId(recentMessageBundleVo, inviteMessageVo);

        return messageMapper.toVo(inviteMessage);
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
