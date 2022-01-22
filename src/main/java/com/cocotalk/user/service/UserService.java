package com.cocotalk.user.service;

import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.dto.request.UserModifyRequest;
import com.cocotalk.user.dto.response.UserResponse;
import com.cocotalk.user.mapper.UserMapper;
import com.cocotalk.user.repository.UserCustomRepositoryImpl;
import com.cocotalk.user.repository.UserRepository;
import com.cocotalk.user.support.GlobalError;
import com.cocotalk.user.support.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserCustomRepositoryImpl userCustomRepository;

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new GlobalException(GlobalError.USER_NOT_EXIST));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        return userMapper.toDto(userRepository.findByEmail(email).orElse(new User()));
    }

    @Transactional(readOnly = true)
    public UserResponse findByCid(String Cid) {
        return userMapper.toDto(userRepository.findByCid(Cid).orElse(new User()));
    }

    @Transactional
    public UserResponse modify(Long id, UserModifyRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new GlobalException(GlobalError.USER_NOT_EXIST));
        user.modify(request);
        return userMapper.toDto(user);
    }

    @Transactional
    public String delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new GlobalException(GlobalError.USER_NOT_EXIST));
        String cid = user.getCid();
        userRepository.deleteById(id);
        return cid;
    }
}
