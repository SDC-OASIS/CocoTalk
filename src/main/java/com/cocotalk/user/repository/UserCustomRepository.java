package com.cocotalk.user.repository;

import com.cocotalk.user.domain.entity.User;

import java.util.Optional;

public interface UserCustomRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByCid(String Cid);
}
