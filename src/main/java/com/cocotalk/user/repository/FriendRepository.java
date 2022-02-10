package com.cocotalk.user.repository;

import com.cocotalk.user.domain.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long>  {
    List<Friend> findByFromUserIdAndHiddenIsFalse(Long fromUserId);
    List<Friend> findByFromUserIdAndHiddenIsTrue(Long fromUserId);

    List<Friend> findByToUserId(Long toUserId);

    Optional<Friend> findByFromUserIdAndToUserId(Long fromUserId, Long toUserId);
    boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);
}
