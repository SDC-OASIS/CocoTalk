package com.cocotalk.repository;

import com.cocotalk.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByCid(String cid);
    Optional<User> findByEmail(String email);
    boolean existsByCid(String cid);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

}
