package com.cocotalk.user.repository;

import com.cocotalk.user.domain.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long>  {
    List<Friend> findByFromUserIdAndHiddenIsFalse(Long fromUserId);
    List<Friend> findByFromUserIdAndHiddenIsTrue(Long fromUserId);
    Friend findByFromUserIdAndToUserId(Long fromUserId, Long toUserId);
}
