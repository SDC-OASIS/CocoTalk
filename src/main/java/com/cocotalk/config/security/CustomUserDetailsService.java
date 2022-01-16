package com.cocotalk.config.security;

import com.cocotalk.repository.UserRepository;
import com.cocotalk.dto.common.UserDetail;
import com.cocotalk.entity.User;

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