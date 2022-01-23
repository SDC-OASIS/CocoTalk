package com.cocotalk.user.service;

import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.domain.vo.UserVo;
import com.cocotalk.user.dto.exception.GlobalError;
import com.cocotalk.user.dto.exception.GlobalException;
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

    public static final GlobalException INVALID_ID = new GlobalException(GlobalError.BAD_REQUEST, "해당 id를 갖는 유저가 존재하지 않습니다.");
    public static final GlobalException INVALID_CID = new GlobalException(GlobalError.BAD_REQUEST, "해당 코코톡 id를 갖는 유저가 존재하지 않습니다.");
    public static final GlobalException INVALID_EMAIL = new GlobalException(GlobalError.BAD_REQUEST, "해당 email을 갖는 유저가 존재하지 않습니다.");

    public static final User EMPTY_USER = new User();

    @Transactional(readOnly = true)
    public List<UserVo> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toVo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserVo findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> INVALID_ID);
        return userMapper.toVo(user);
    }

    @Transactional(readOnly = true)
    public UserVo findByCid(String cid) {
        User user = userRepository.findByCid(cid).orElseThrow(() -> INVALID_CID);
        return userMapper.toVo(user);
    }

    @Transactional(readOnly = true)
    public UserVo findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> INVALID_EMAIL);
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
        String cid = user.getCid();
        userRepository.delete(user);
        return cid;
    }
}
