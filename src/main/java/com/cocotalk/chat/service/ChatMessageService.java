package com.cocotalk.chat.service;


import com.cocotalk.chat.domain.entity.message.ChatMessage;
import com.cocotalk.chat.domain.entity.message.InviteMessage;
import com.cocotalk.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    private static final ChatMessage emptyMessage = new ChatMessage();

    public ChatMessage find(ObjectId id) {
        return chatMessageRepository.findById(id).orElse(emptyMessage);
    }

    public ObjectId saveChatMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage).getId();
    }

    public ObjectId saveInviteMessage(InviteMessage inviteMessage) {
        return chatMessageRepository.save(inviteMessage).getId();
    }
}
