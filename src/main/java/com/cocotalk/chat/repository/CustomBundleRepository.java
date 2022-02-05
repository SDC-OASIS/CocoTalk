package com.cocotalk.chat.repository;

import com.cocotalk.chat.domain.entity.message.MessageBundle;
import org.bson.types.ObjectId;

import java.util.Optional;

public interface CustomBundleRepository {
     MessageBundle findBundleAndSlice(ObjectId id, int start, int unit);
     Optional<MessageBundle> findBeforeBundleAndSlice(ObjectId roomId, ObjectId currentId, int start, int diff);
}
