package com.cocotalk.chat.service;

import com.cocotalk.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;

//    public ObjectId save() {
//        return messageRepository.save();
//    }
}
