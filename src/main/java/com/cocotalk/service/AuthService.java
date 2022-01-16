package com.cocotalk.service;

import com.cocotalk.config.ValidationCheck;
import com.cocotalk.dto.common.TokenDto;
import com.cocotalk.dto.signin.SigninInput;
import com.cocotalk.dto.signup.SignupInput;
import com.cocotalk.dto.signup.SignupOutput;
import com.cocotalk.entity.Provider;
import com.cocotalk.entity.User;
import com.cocotalk.repository.UserRepository;
import com.cocotalk.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.cocotalk.response.ResponseStatus.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService redisService;

    @Value("${jwt.token.exp.refresh}")
    long REFRESH_TOKEN_VALIDITY_SECONDS;

    public ResponseEntity<Response<TokenDto>> signin(SigninInput signinInput) {
        // 1. 값 형식 체크
        if (signinInput == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(NO_VALUES));
        if (!ValidationCheck.isValid(signinInput.getCid()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(BAD_COCOTALK_ID));
        if (!ValidationCheck.isValid(signinInput.getPassword()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(BAD_PASSWORD_VALUE));

        // 2. user 정보 가져오기
        User user;
        try {
            String cid = signinInput.getCid();
            user = userRepository.findByCid(cid).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(NOT_FOUND_USER));
            } else if (!passwordEncoder.matches(signinInput.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(FAILED_TO_SIGN_IN));
            }
        } catch (Exception e) {
            log.error("[auth/signin/post] database error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(DATABASE_ERROR));
        }

        user.setLoginedAt(LocalDateTime.now());

        // 3. token 생성
        String accessToken;
        String refreshToken;
        try {
            accessToken = jwtService.createAccessToken(user.getCid());
            refreshToken = jwtService.createRefreshToken(user.getCid());
            if (accessToken.isEmpty() || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(FAILED_TO_CREATE_TOKEN));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(FAILED_TO_CREATE_TOKEN));
        }

        // 4. redis에 refresh token 기록
        redisService.setDataExpire(refreshToken, user.getCid(), REFRESH_TOKEN_VALIDITY_SECONDS);

        // 5. 결과 return
        TokenDto tokenDto = TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(tokenDto, SUCCESS_SIGN_IN));
    }

    @Transactional
    public ResponseEntity<Response<SignupOutput>> signup(SignupInput signupInput) {
        // 1. 값 형식 체크
        if (signupInput == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(NO_VALUES));
        }
        if (!ValidationCheck.isValid(signupInput.getCid())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(BAD_COCOTALK_ID));
        }
        if (!ValidationCheck.isValid(signupInput.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(BAD_PASSWORD_VALUE));
        }
        if (!ValidationCheck.isValid(signupInput.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(BAD_NAME_VALUE));
        }
        if (!ValidationCheck.isValid(signupInput.getNickname())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(BAD_NICKNAME_VALUE));
        }
        if (!ValidationCheck.isValid(signupInput.getPhone())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(BAD_PHONE_VALUE));
        }
        if (!ValidationCheck.isValidProvider(signupInput.getProvider())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(BAD_PROVIDER_VALUE));
        }
        if (!ValidationCheck.isValid(signupInput.getProviderId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(BAD_PROVIDER_ID_VALUE));
        }
        if (signupInput.getStatus() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(BAD_STATUS_VALUE));
        }

        // 2. 유저 생성
        User user;
        try {
            String cid = signupInput.getCid();
            String phone = signupInput.getPhone();
            String email = signupInput.getEmail();
            boolean existCid = userRepository.existsByCid(cid);
            boolean existPhone = userRepository.existsByPhone(phone);
            boolean existEmail = userRepository.existsByEmail(email);
            String password = passwordEncoder.encode(signupInput.getPassword());
            // 중복 제어
            if (existCid) { 
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(EXISTS_COCOTALK_ID));
            } else if (existPhone) { 
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(EXISTS_PHONE));
            } else if (existEmail) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(EXISTS_EMAIL));
            }

            user = User.builder()
                    .cid(signupInput.getCid())
                    .password(password)
                    .name(signupInput.getName())
                    .nickname(signupInput.getNickname())
                    .birth(signupInput.getBirth())
                    .phone(phone)
                    .email(email)
                    .provider(Provider.valueOf(signupInput.getProvider()))
                    .providerId(signupInput.getProviderId())
                    .status(signupInput.getStatus())
                    .profile(signupInput.getProfile())
                    .build();

            user = userRepository.save(user);

        } catch (Exception e) {
            log.error("[auth/signup/post] database error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(DATABASE_ERROR));
        }
        // 3. 결과 return
        SignupOutput signupOutput = SignupOutput.builder()
                .cid(user.getCid())
                .name(user.getName())
                .nickname(user.getNickname())
                .birth(user.getBirth())
                .phone(user.getPhone())
                .email(user.getEmail())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .build();
//        SignupOutput signupOutput = SignupOutput.toDto(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(signupOutput, CREATED_USER));
    }

    public ResponseEntity<Response<Object>> signout() {
        String refreshToken = jwtService.getRefreshToken();
        if(ValidationCheck.isValid(refreshToken))
            redisService.deleteData(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(SUCCESS));
    }

    /**
     * 해당 refresh token이 redis의 값과 일치한 경우 token 재발급
     * @return ResponseEntity<Response<TokenDto>>
     */
    public ResponseEntity<Response<TokenDto>> reissue() {
        String refreshToken = jwtService.getRefreshToken();
        System.out.println(redisService.getData(refreshToken)+","+redisService.getData("없는값"));
        if(!ValidationCheck.isValid(redisService.getData(refreshToken)))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response<>(UNAUTHORIZED_REFRESH_TOKEN));
        try{
            String userCid = jwtService.getClaims(refreshToken).getBody().get("userCid", String.class);
            //redis에서 기존 refresh token 삭제
            redisService.deleteData(refreshToken);
            TokenDto token = TokenDto.builder()
                    .accessToken(jwtService.createAccessToken(userCid))
                    .refreshToken(jwtService.createRefreshToken(userCid))
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(token, SUCCESS_TO_REISSUE_TOKEN));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response<>(UNAUTHORIZED_REFRESH_TOKEN));
        }
    }
}
