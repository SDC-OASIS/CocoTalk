package com.cocotalk.user.repository;

import com.cocotalk.user.domain.entity.User;

import java.util.Optional;

public interface UserCustomRepository {
    Optional<User> find(String cid, String email, String phones);
}
