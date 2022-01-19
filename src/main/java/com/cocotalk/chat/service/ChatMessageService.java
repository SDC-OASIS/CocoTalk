package com.cocotalk.chat.service;


import com.cocotalk.chat.document.message.ChatMessage;
import com.cocotalk.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    public ObjectId save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage).getId();
    }
}
