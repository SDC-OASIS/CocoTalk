package com.cocotalk.chat.service;

import com.cocotalk.chat.domain.entity.message.MessageBundle;
import com.cocotalk.chat.domain.vo.ChatMessageVo;
import com.cocotalk.chat.domain.vo.MessageBundleVo;
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

    public static final MessageBundle emptyMessageBundle = new MessageBundle();
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

    public MessageBundleVo findSlice(ObjectId id, int start, int unit) {
        MessageBundle messageBundle = messageBundleRepository.findSlice(id, start, unit);
        return messageBundleMapper.toVo(messageBundle);
    }

    public MessageBundleVo findJustBeforeAndSlice(ObjectId roomId, ObjectId currentBundleId, int start, int diff) {
        MessageBundle messageBundle = messageBundleRepository.findJustBeforeAndSlice(
                roomId,
                currentBundleId,
                start,
                diff)
                .orElse(emptyMessageBundle);
        return messageBundleMapper.toVo(messageBundle);
    }

    public MessageBundleVo saveMessageId(MessageBundleVo messageBundleVo, ChatMessageVo chatMessageVo) {
        messageBundleVo.getMessageIds().add(chatMessageVo.getId());
        MessageBundle messageBundle = messageBundleMapper.toEntity(messageBundleVo);
        messageBundle.setCount(messageBundleVo.getCount() + 1);
        return messageBundleMapper.toVo(messageBundleRepository.save(messageBundle));
    }
}
