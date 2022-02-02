package com.cocotalk.user.service;

import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.domain.vo.UserVo;
import com.cocotalk.user.exception.CustomError;
import com.cocotalk.user.exception.CustomException;
import com.cocotalk.user.dto.request.UserModifyRequest;
import com.cocotalk.user.repository.UserCustomRepositoryImpl;
import com.cocotalk.user.repository.UserRepository;
import com.cocotalk.user.utils.mapper.UserMapper;
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

    public static final CustomException INVALID_USERID =
            new CustomException(CustomError.BAD_REQUEST, "해당 userId를 갖는 유저가 존재하지 않습니다.");

    public static final User EMPTY_USER = new User();

    @Transactional(readOnly = true)
    public List<UserVo> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toVo)
                .collect(Collectors.toList());
    }

    public UserVo findByAccessToken(User user) {
        return userMapper.toVo(user);
    }

    @Transactional(readOnly = true)
    public UserVo findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> INVALID_USERID);
        return userMapper.toVo(user);
    }

    @Transactional(readOnly = true)
    public UserVo findByCid(String cid) {
        User user = userRepository.findByCid(cid).orElse(EMPTY_USER);
        return userMapper.toVo(user);
    }

    @Transactional(readOnly = true)
    public UserVo findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(EMPTY_USER);
        return userMapper.toVo(user);
    }

    @Transactional(readOnly = true)
    public UserVo find(String cid, String email, String phone) {
        return userMapper.toVo(userCustomRepository.find(cid, email, phone).orElse(EMPTY_USER));
    }

    @Transactional(readOnly = true)
    public List<UserVo> findByPhones(List<String> phones) {
        return phones.stream()
                .map(phone -> userRepository.findByPhone(phone).orElse(EMPTY_USER))
                .map(userMapper::toVo)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserVo modify(User user, UserModifyRequest request) {
        user.modify(request);
        userRepository.save(user);
        return userMapper.toVo(user);
    }

    @Transactional
    public String delete(User user) {
        String message = String.format("유저 %s이 삭제되었습니다.", user.getCid());
        userRepository.delete(user);
        return message;
    }
}
