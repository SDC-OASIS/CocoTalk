package com.cocotalk.auth.config.security;

import com.cocotalk.auth.entity.User;
import com.cocotalk.auth.repository.UserRepository;
import com.cocotalk.auth.dto.common.UserDetail;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String cid) throws UsernameNotFoundException {
        User user = userRepository.findByCid(cid).orElse(null);
        if (user == null) {
            return null;
        }
        return UserDetail.builder().id(user.getId()).user(user).build();
    }

}