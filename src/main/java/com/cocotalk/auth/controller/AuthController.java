package com.cocotalk.auth.controller;

import com.cocotalk.auth.dto.common.ClientInfo;
import com.cocotalk.auth.dto.common.TokenDto;
import com.cocotalk.auth.dto.email.issue.IssueInput;
import com.cocotalk.auth.dto.signup.SignupInput;
import com.cocotalk.auth.service.AuthService;
import com.cocotalk.auth.dto.email.issue.IssueOutput;
import com.cocotalk.auth.dto.email.validation.ValidationInput;
import com.cocotalk.auth.dto.common.ValidationDto;
import com.cocotalk.auth.dto.signin.SigninInput;
import com.cocotalk.auth.dto.signup.SignupOutput;
import com.cocotalk.auth.dto.common.response.Response;
import com.cocotalk.auth.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

/**
 * 인증에 필요한 API
 * clientInfo는 ClientArgumentResolver를 통해 자동으로 들어옴
 */
@Tag(name = "인증 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 API [POST] /signin
     * 
     * @param signinInput 로그인에 필요한 정보
     * @param clientInfo 요청자의 client 정보 ( MOBILE/WEB을 별도로 관리하기 위해 필요 )
     * @return 발급된 accesstoken과 refreshtoken
     */
    @Operation(summary  = "로그인")
    @PostMapping("/signin")
    public ResponseEntity<Response<TokenDto>> signin(@Parameter(hidden = true) ClientInfo clientInfo, @RequestBody @Valid SigninInput signinInput) {
        return authService.signin(clientInfo, signinInput);
    }

    /**
     * 로그아웃 API [GET] /signout
     *
     * @param clientInfo 요청자의 client 정보 ( MOBILE/WEB을 별도로 관리하기 위해 필요 )
     */
    @Operation(summary = "로그아웃")
    @GetMapping("/signout")
    public ResponseEntity<Response<Object>> signout(ClientInfo clientInfo) {
        return authService.signout(clientInfo);
    }

    /**
     * 회원가입 API [POST] /signup
     *
     * @param signUpInput 회원가입에 필요한 정보
     * @return 가입된 회원 정보
     */
    @Operation(summary = "회원가입")
    @PostMapping(value = "/signup", consumes = {"multipart/form-data"})
    public ResponseEntity<Response<SignupOutput>> signup(@Valid SignupInput signUpInput) {
        log.info("[POST] /api/users/signup : " + signUpInput);
        return authService.signup(signUpInput);
    }


    /**
     * ACCESS TOKEN 재발급 API [GET] /reissue
     *
     * @param clientInfo 요청자의 client 정보 ( MOBILE/WEB을 별도로 관리하기 위해 필요 )
     * @return 발급된 accesstoken과 refreshtoken
     */
    @Operation(summary = "ACCESS TOKEN 재발급")
    @GetMapping("/reissue")
    public ResponseEntity<Response<TokenDto>> reissue(ClientInfo clientInfo) {
        log.info("[GET] /api/users/reissue");
        return authService.reissue(clientInfo);
    }

    /**
     * 이메일 인증 코드 보내기 API [POST] /email/issue
     *
     * @param emailInput 인증 코드를 보낼 이메일 정보
     * @return 전송한 인증코드의 만료시간이 담긴 모델
     */
    @Operation(summary = "Eamil 인증 코드 발송")
    @PostMapping("/email/issue")
    public ResponseEntity<Response<IssueOutput>> sendMail(@RequestBody @Valid IssueInput emailInput) {
        log.info("[POST] /users/email");
        return authService.sendMail(emailInput);
    }

    /**
     * 이메일 인증 코드 확인 API [POST] /email/validation
     *
     * @param validationInput 확인할 email과 code 정보
     * @return 해당 이메일의 인증코드가 유효한지에 대한 결과
     */
    @Operation(summary = "Eamil 인증 코드 확인")
    @PostMapping("/email/validation")
    public ResponseEntity<Response<ValidationDto>> checkMail(@RequestBody @Valid ValidationInput validationInput) {
        log.info("[POST] /email/validation");
        return authService.checkMail(validationInput);
    }

    /**
     * 마지막으로 로그인한 기기 검증 API [GET] /device
     * request의 accesstoken 속 fcmtoken과 redis에 보관된 마지막 접속자의 refreshtoken속 fcmtoken을 비교
     *
     * @param clientInfo 요청자의 client 정보 ( MOBILE/WEB을 별도로 관리하기 위해 필요 )
     * @return 마지막으로 로그인힌 기기가 맞는지에 대한 결과
     */
    @Operation(summary = "마지막으로 로그인한 기기가 맞는지 체크")
    @GetMapping("/device")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<Response<ValidationDto>>checkLastly(ClientInfo clientInfo) {
        log.info("[GET] /device");
        return authService.checkLastly(clientInfo);
    }

}
