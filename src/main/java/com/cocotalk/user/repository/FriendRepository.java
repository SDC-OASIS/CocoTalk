package com.cocotalk.user.repository;

import com.cocotalk.user.domain.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long>  {
}
