package com.cocotalk.user.service;

import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.domain.vo.ProfilePayload;
import com.cocotalk.user.domain.vo.UserVo;
import com.cocotalk.user.dto.request.UserModifyRequest;
import com.cocotalk.user.dto.request.profile.BgUpdateRequest;
import com.cocotalk.user.dto.request.profile.ImgUpdateRequest;
import com.cocotalk.user.dto.request.profile.MessageUpdateRequest;
import com.cocotalk.user.exception.CustomError;
import com.cocotalk.user.exception.CustomException;
import com.cocotalk.user.repository.FriendRepository;
import com.cocotalk.user.repository.UserRepository;
import com.cocotalk.user.utils.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final S3Service s3Service;

    public static final CustomException INVALID_USER_ID =
            new CustomException(CustomError.BAD_REQUEST, "해당 userId를 갖는 유저가 존재하지 않습니다.");
    public static final CustomException CANNOT_FIND_SELF =
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
        User user = userRepository.findById(id).orElseThrow(() -> INVALID_USER_ID);
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

    @Transactional
    public UserVo updateProfileImg(User user, ImgUpdateRequest request) {

        s3Service.deleteProfileImg(user.getId());

        String fileUrl;
        // 이미지가 있으면 S3에 업로드
        if(request != null
                && request.getProfileImg() != null
                && request.getProfileImgThumb() != null
                && !request.getProfileImg().isEmpty()
                && !request.getProfileImgThumb().isEmpty()) {
            fileUrl = s3Service.uploadProfileImg(request.getProfileImg(), request.getProfileImgThumb(), user.getId());
        } else {
            fileUrl = null;
        }

        //json -> object
        ProfilePayload profilePayload = ProfilePayload.toObject(user.getProfile());
        profilePayload.setProfile(fileUrl);

        // objcet -> json
        String profile =  ProfilePayload.toJSON(profilePayload);

        user.setProfile(profile);
        userRepository.save(user);
        return userMapper.toVo(user);
    }

    @Transactional
    public UserVo updateProfileBg(User user, BgUpdateRequest request) {

        s3Service.deleteProfileBg(user.getId());

        String fileUrl = null;
        // 이미지가 있으면 S3에 업로드
        if(request != null && request.getBgImg() != null && !request.getBgImg().isEmpty())
            fileUrl = s3Service.uploadProfileBg(request.getBgImg(), user.getId());

        //json -> object
        ProfilePayload profilePayload = ProfilePayload.toObject(user.getProfile());
        profilePayload.setBackground(fileUrl);

        // objcet -> json
        String profile =  ProfilePayload.toJSON(profilePayload);
        user.setProfile(profile);

        userRepository.save(user);
        return userMapper.toVo(user);
    }

    @Transactional
    public UserVo updateProfileMsg(User user, MessageUpdateRequest request) {
        log.info("[updateProfileMsg/request] : " + request);
        //json -> object
        ProfilePayload profilePayload = ProfilePayload.toObject(user.getProfile());
        profilePayload.setMessage(request.getMessage());

        // objcet -> json
        String profile =  ProfilePayload.toJSON(profilePayload);
        user.setProfile(profile);

        userRepository.save(user);
        return userMapper.toVo(user);
    }
}
