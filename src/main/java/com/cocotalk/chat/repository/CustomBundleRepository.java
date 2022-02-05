package com.cocotalk.chat.repository;

import com.cocotalk.chat.domain.entity.message.MessageBundle;
import org.bson.types.ObjectId;

import java.util.Optional;

public interface CustomBundleRepository {
    Optional<MessageBundle> findJustBeforeAndSlice(ObjectId roomId, ObjectId currentMessageBundleId, int start, int diff);
}
