package com.cocotalk.auth.service;

import com.cocotalk.auth.application.AuthException;
import com.cocotalk.auth.dto.common.*;
import com.cocotalk.auth.dto.signup.SignupInput;
import com.cocotalk.auth.repository.UserRepository;
import com.cocotalk.auth.dto.common.response.ResponseStatus;
import com.cocotalk.auth.dto.email.issue.IssueInput;
import com.cocotalk.auth.dto.email.issue.IssueOutput;
import com.cocotalk.auth.dto.email.validation.ValidationInput;
import com.cocotalk.auth.dto.signin.SigninInput;
import com.cocotalk.auth.dto.signup.SignupOutput;
import com.cocotalk.auth.entity.User;
import com.cocotalk.auth.entity.mapper.UserMapper;
import com.cocotalk.auth.dto.common.response.Response;
import com.cocotalk.auth.utils.JwtUtils;
import com.cocotalk.auth.utils.SHA256Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

import static com.cocotalk.auth.dto.common.response.ResponseStatus.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final JavaMailSender mailSender;
    private final S3Service s3Service;

    @Value("${api.push}")
    String pushApiUrl;

    @Value("${mail.exp}")
    long mailCodeExp;
    public ResponseEntity<Response<TokenDto>> signin(ClientType clientType, SigninInput signinInput) {
        // 1. user 정보 가져오기
        log.info("[signin/ClientType] : "+ clientType);
        log.info("[signin/SigninInput] : "+ signinInput);
        User user;
        try {
            user = userRepository.findByCid(signinInput.getCid()).orElse(null);

            if (user == null || !SHA256Utils.getEncrypt(signinInput.getPassword()).equals(user.getPassword())) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<>(BAD_REQUEST));
            }
            user.setLoggedinAt(LocalDateTime.now());
        } catch (Exception e){
            throw new AuthException(DATABASE_ERROR);
        }

        Long userId = user.getId();

        // 2. 해당 user의 device 정보 가져오기
        DeviceDto device = getFcmToken(userId, clientType);
        if(device==null)
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(NOT_FOUND_FCM_TOKEN));

        // 3. token 생성
        String accessToken;
        String refreshToken;
        String fcmToken = device.getToken();
        log.info("[signin/fcmToken] : "+fcmToken);

        try {
            accessToken = JwtUtils.createAccessToken(userId, fcmToken);
            refreshToken = JwtUtils.createRefreshToken(userId, fcmToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(BAD_REQUEST));
        }

        // 3. redis에 refresh token 기록
        redisService.setRefreshToken(clientType, userId, refreshToken);

        // 4. 결과 return
        TokenDto tokenDto = TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(tokenDto, ResponseStatus.SUCCESS));
    }

    @Transactional
    public ResponseEntity<Response<SignupOutput>> signup(SignupInput signupInput) {
        log.info("[signup/signupInput] : "+signupInput);
        SignupInput.ProfileInfo profile = signupInput.getProfileInfo();
        // 2. 유저 생성
        User user;
        try {
            // 중복 제어
            boolean exists = userRepository.existsByCid(profile.getCid())
                    || userRepository.existsByPhone(profile.getPhone())
                    || userRepository.existsByEmail(profile.getEmail());
            if (exists) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<>(EXISTS_INFO));
            }

            //pk 생성
            User tmpUser = userMapper.toEntity(profile);
            user = userRepository.save(tmpUser);

            //s3에 이미지 저장
            String imgUrl = s3Service.uploadProfileImage(signupInput.getProfileImg(), user.getId());
            s3Service.uploadThumbnail(signupInput.getProfileImgThumb(), user.getId());

            user.setPassword(SHA256Utils.getEncrypt(profile.getPassword()));
            user.setProfile(imgUrl);
            userRepository.save(user);

        } catch (Exception e) {
            log.error("[signup/post] database error", e);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(DATABASE_ERROR));
        }
        // 3. 결과 return
        SignupOutput signupOutput = userMapper.toDto(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(signupOutput, CREATED));
    }

    public ResponseEntity<Response<Object>> signout(ClientType clientType) {
        String refreshToken = JwtUtils.getRefreshToken();
        if(refreshToken!=null) {
            Long userId = JwtUtils.getPayload(refreshToken).getUserId();
            if(userId!=null) redisService.deleteRefreshToken(clientType, userId);
        }
        /*
            소켓 서버에서, 다른 기기 로그아웃 처리 요청
         */
        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(null, SUCCESS));
    }

    /**
     * 해당 refresh token이 redis의 값과 일치한 경우 token 재발급
     * @return ResponseEntity<Response<TokenDto>>
     */
    public ResponseEntity<Response<TokenDto>> reissue(ClientType clientType) {
        String refreshToken = JwtUtils.getRefreshToken();
        if(refreshToken==null) {
            log.error("[reissue] X-REFRESH-TOKEN is null");
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(ResponseStatus.UNAUTHORIZED));
        }
        try{
            // 1. refresh token이 서버와 일치하는지 확인
            Long userId = JwtUtils.getPayload(refreshToken).getUserId();
            String storeRefreshToken = redisService.getRefreshToken(clientType, userId);
            if(!refreshToken.equals(storeRefreshToken)) {
                log.error("[reissue] refreshToken is not equals as storeRefreshToken");
                return ResponseEntity.status(HttpStatus.OK).body(new Response<>(ResponseStatus.UNAUTHORIZED));
            }
            // 2. 해당 user의 device 정보 가져오기
            DeviceDto device = getFcmToken(userId, clientType);
            if(device==null)
                return ResponseEntity.status(HttpStatus.OK).body(new Response<>(NOT_FOUND_FCM_TOKEN));

            // 3. token 생성
            String fcmToken = device.getToken();
            String newRefreshToken = JwtUtils.createRefreshToken(userId, fcmToken);

            // 4. redis에 refresh token 갱신
            redisService.setRefreshToken(clientType,userId,newRefreshToken);
            
            // 5. 결과 반환
            TokenDto token = TokenDto.builder()
                    .accessToken(JwtUtils.createAccessToken(userId,fcmToken))
                    .refreshToken(newRefreshToken)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(token, ResponseStatus.SUCCESS));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(ResponseStatus.UNAUTHORIZED));
        }
    }

    public ResponseEntity<Response<IssueOutput>> sendMail(IssueInput issueInput) {
        log.info("[sendMail/IssueInput] : "+issueInput);
        IssueOutput emailOutput;
        try {
            // 1. 인증 메일 전송
            String generatedString = RandomStringUtils.random(10, true, true);
            MimeMessage message = mailSender.createMimeMessage();
            message.addRecipients(Message.RecipientType.TO, issueInput.getEmail());//보내는 대상
            message.setSubject("[코코톡] 이메일 인증번호입니다.");
            String msgg= "<div style='margin:100px;'>" +
                    "<h1> 안녕하세요 코코톡입니다 </h1>" +
                    "<br>" +
                    "<p>아래 코드를 입력해주세요<p>" +
                    "<br>" +
                    "<div align='center' style='background-color: #aecdb3a1; border-radius: 60% 10%; padding: 10px; font-family:verdana';>" +
                    "<h3 style='color:#747474;'> 코드입니다</h3>" +
                    "<div style='font-size:130%'>" +
                    "CODE : <strong>" +
                    generatedString +
                    "</strong><div><br/>" +
                    "</div>";
            message.setText(msgg, "utf-8", "html");

            LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(mailCodeExp);
            emailOutput = IssueOutput.builder().expirationDate(expirationDate).build();
            mailSender.send(message);

            // 2. redis에 code 기록
            redisService.setEmailCode(issueInput.getEmail(), generatedString);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(ResponseStatus.BAD_REQUEST));
        }
        // 3. 결과 return
        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(emailOutput, ResponseStatus.SUCCESS));
    }

    public ResponseEntity<Response<ValidationDto>> checkMail(ValidationInput validationInput) {
        log.info("[checkMail/ValidationInput] : "+validationInput);
        String code = redisService.getEmailCode(validationInput.getEmail());
        Boolean res = code!=null && validationInput.getCode().equals(code);
        ValidationDto validationOutput = ValidationDto.builder().isValid(res).build();
        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(validationOutput, ResponseStatus.SUCCESS));
    }


    public ResponseEntity<Response<ValidationDto>> checkLastly(ClientType clientType) {
        String accessToken = JwtUtils.getAccessToken();
        if(accessToken==null)
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(ResponseStatus.UNAUTHORIZED));
        /*
         [인증 요청한 기기]의 FCM Token과
         서버에 기록된 [마지막 로그인 기기]의 FCM Token
         일치하는지 비교
         */
        TokenPayload currTP = JwtUtils.getPayload(accessToken);

        String lastlyRToken = redisService.getRefreshToken(clientType, currTP.getUserId()); //마지막 로그인 유저의 refresh token
        String lastlyFToken = JwtUtils.getPayload(lastlyRToken).getFcmToken(); //lastlyRToken로 마지막 로그인한 기기 fcm token 구함
        Boolean res = currTP.getFcmToken().equals(lastlyFToken);
        ValidationDto validationDto = ValidationDto.builder().isValid(res).build();
        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(validationDto, ResponseStatus.SUCCESS));
    }

    private DeviceDto getFcmToken(long userId, ClientType clientType){
        log.info("[getFcmToken/userId] : "+userId);
        WebClient webClient = WebClient.create(pushApiUrl);
        try{
            DeviceDto device = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path("/device")
                            .queryParam("userId",userId)
                            .queryParam("type",clientType)
                            .build()
                    )
                    .retrieve()
                    .bodyToFlux(DeviceDto.class)
                    .blockFirst();
            log.info("[getFcmToken/deviceDto] :" + device);
            return device;
        }catch (Exception e){
            throw new AuthException(SERVER_ERROR,e);
        }
    }
}
