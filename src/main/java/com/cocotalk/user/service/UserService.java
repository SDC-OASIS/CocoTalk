package com.cocotalk.user.service;

import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.dto.request.UserModifyRequest;
import com.cocotalk.user.repository.UserCustomRepositoryImpl;
import com.cocotalk.user.repository.UserRepository;
import com.cocotalk.user.support.GlobalError;
import com.cocotalk.user.support.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserCustomRepositoryImpl userCustomRepository;

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new GlobalException(GlobalError.USER_NOT_EXIST));
    }

    @Transactional
    public User modifyById(Long id, UserModifyRequest request){
        User user = this.findById(id);
        user.modify(request);
        return user;
    }

    @Transactional
    public String deleteById(Long id){
        User user = this.findById(id);
        String cid = user.getCid();
        userRepository.deleteById(id);
        return cid;
    }
}
