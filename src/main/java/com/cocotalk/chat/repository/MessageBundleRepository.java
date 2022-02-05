package com.cocotalk.chat.repository;

import com.cocotalk.chat.domain.entity.message.MessageBundle;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageBundleRepository extends MongoRepository<MessageBundle, ObjectId>, CustomBundleRepository {
    @Query(value = "{ _id: ?0 }, { messageIds: { $slice: ?1, ?2 } }")
    MessageBundle findSlice(ObjectId messageBundleId, int start, int unit);

//    @Query(value = "{ roomId: ?0, _id : { $lt : ?1 } }, { messageIds : { $slice: [?2, ?3] } }")
//    List<MessageBundle> findJustBeforeAndSlice(ObjectId roomId, ObjectId currentMessageBundleId, int start, int unit, Pageable pageable);
}
