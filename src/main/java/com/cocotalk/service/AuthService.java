package com.cocotalk.service;

import com.cocotalk.config.ValidationCheck;
import com.cocotalk.dto.common.TokenDto;
import com.cocotalk.dto.email.issue.IssueInput;
import com.cocotalk.dto.email.issue.IssueOutput;
import com.cocotalk.dto.email.validation.ValidationInput;
import com.cocotalk.dto.signin.SigninInput;
import com.cocotalk.dto.signup.SignupInput;
import com.cocotalk.dto.signup.SignupOutput;
import com.cocotalk.entity.User;
import com.cocotalk.entity.mapper.UserMapper;
import com.cocotalk.repository.UserRepository;
import com.cocotalk.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Date;

import static com.cocotalk.response.ResponseStatus.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final JavaMailSender mailSender;

    @Value("${jwt.token.exp.refresh}")
    long refreshTokenExp;

    @Value("${mail.exp}")
    long mailCodeExp;

    public ResponseEntity<Response<TokenDto>> signin(SigninInput signinInput) {
        // 1. user 정보 가져오기
        User user;
        try {
            String cid = signinInput.getCid();
            user = userRepository.findByCid(cid).orElse(null);
            if (user == null || !passwordEncoder.matches(signinInput.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<>(BAD_REQUEST));
            }
        } catch (Exception e) {
            log.error("[auth/signin/post] database error", e);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(DATABASE_ERROR));
        }

        user.setLoginedAt(LocalDateTime.now());

        // 2. token 생성
        String accessToken;
        String refreshToken;
        try {
            accessToken = jwtService.createAccessToken(user.getCid());
            refreshToken = jwtService.createRefreshToken(user.getCid());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(BAD_REQUEST));
        }

        // 3. redis에 refresh token 기록
        redisService.setDataExpire(refreshToken, user.getCid(), refreshTokenExp);

        // 4. 결과 return
        TokenDto tokenDto = TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(tokenDto, SUCCESS));
    }

    @Transactional
    public ResponseEntity<Response<SignupOutput>> signup(SignupInput signupInput) {

        if(!ValidationCheck.isValidProvider(signupInput.getProvider()))
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(BAD_REQUEST));

        // 1. 유저 생성
        User user;
        try {
            // 중복 제어
            boolean exists = userRepository.existsByCid(signupInput.getCid())
                    || userRepository.existsByPhone(signupInput.getPhone())
                    || userRepository.existsByEmail(signupInput.getEmail());
            if (exists) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<>(EXISTS_INFO));
            }

            user = userMapper.toEntity(signupInput);
            user.setPassword(passwordEncoder.encode(signupInput.getPassword()));
            user = userRepository.save(user);

        } catch (Exception e) {
            log.error("[auth/signup/post] database error", e);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(DATABASE_ERROR));
        }
        // 3. 결과 return
        SignupOutput signupOutput = userMapper.toDto(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(signupOutput, CREATED));

    }

    public ResponseEntity<Response<Object>> signout() {
        String refreshToken = jwtService.getRefreshToken();
        if(ValidationCheck.isValid(refreshToken))
            redisService.deleteData(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(null,SUCCESS));
    }

    /**
     * 해당 refresh token이 redis의 값과 일치한 경우 token 재발급
     * @return ResponseEntity<Response<TokenDto>>
     */
    public ResponseEntity<Response<TokenDto>> reissue() {
        String refreshToken = jwtService.getRefreshToken();
        System.out.println(redisService.getData(refreshToken)+","+redisService.getData("없는값"));
        if(!ValidationCheck.isValid(redisService.getData(refreshToken)))
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(UNAUTHORIZED));
        try{
            String userCid = jwtService.getClaims(refreshToken).getBody().get("userCid", String.class);
            //redis에서 기존 refresh token 삭제
            redisService.deleteData(refreshToken);
            TokenDto token = TokenDto.builder()
                    .accessToken(jwtService.createAccessToken(userCid))
                    .refreshToken(jwtService.createRefreshToken(userCid))
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(token, SUCCESS));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(UNAUTHORIZED));
        }
    }

    public ResponseEntity<Response<IssueOutput>> sendMail(IssueInput issueInput) {

        IssueOutput emailOutput;
        try {
            // 1. 인증 메일 전송
            String generatedString = RandomStringUtils.random(10, true, true);
            MimeMessage message = mailSender.createMimeMessage();
            message.addRecipients(Message.RecipientType.TO, issueInput.getEmail());//보내는 대상
            message.setSubject("[코코톡] 이메일 인증번호입니다.");
            String msgg= "<div style='margin:100px;'>" +
                    "<h1> 안녕하세요  코코톡입니다 </h1>" +
                    "<br>" +
                    "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>" +
                    "<br>" +
                    "<div align='center' style='background-color: #aecdb3a1; border-radius: 60% 10%; padding: 10px; font-family:verdana';>" +
                    "<h3 style='color:#747474;'>회원가입 코드입니다</h3>" +
                    "<div style='font-size:130%'>" +
                    "CODE : <strong>" +
                    generatedString +
                    "</strong><div><br/>" +
                    "</div>";
            message.setText(msgg, "utf-8", "html");

            Date expirationDate = new Date(new Date().getTime() + mailCodeExp * 1000);
            emailOutput = IssueOutput.builder().expirationDate(expirationDate).build();
            mailSender.send(message);

            // 2. redis에 코드 기록
            redisService.setDataExpire(issueInput.getEmail(), generatedString, mailCodeExp);

        } catch (Exception e) {
            log.error("[auth/email/post] send email error", e);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(BAD_REQUEST));
        }
        // 3. 결과 return
        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(emailOutput, SUCCESS));
    }


    public ResponseEntity<Response<Boolean>> checkMail(ValidationInput validationInput) {
        String code = redisService.getData(validationInput.getEmail());
        Boolean res = code!=null && validationInput.getCode().equals(code);
        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(res, SUCCESS));
    }

}
