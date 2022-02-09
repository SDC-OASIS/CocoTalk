package com.cocotalk.auth.controller;

import com.cocotalk.auth.dto.common.ClientType;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Tag(name = "인증 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 API [POST] /api/auth/signin
     *
     * @return ResponseEntity<Response<TokenDto>>
     */
    @Operation(summary  = "로그인")
    @PostMapping("/signin")
    public ResponseEntity<Response<TokenDto>> signin(ClientType clientType, @RequestBody @Valid SigninInput signinInput) {
        return authService.signin(clientType, signinInput);
    }

    /**
     * 로그아웃 API [POST] /signout
     *
     * @return ResponseEntity<Response<Object>>
     */
    @Operation(summary = "로그아웃")
    @GetMapping("/signout")
    public ResponseEntity<Response<Object>> signout(ClientType clientType) {
        return authService.signout(clientType);
    }

    /**
     * 회원가입 API [POST] /signup
     *
     * @return ResponseEntity<Response<SignUpOutput>>
     */
    // Body
    @Operation(summary = "회원가입")
    @PostMapping(value = "/signup", consumes = {"multipart/form-data"})
//    @PostMapping("/signup")
    public ResponseEntity<Response<SignupOutput>> signup(@Valid SignupInput signUpInput) {
        log.info("[POST] /api/users/signup");
        return authService.signup(signUpInput);
    }


    /**
     * ACCESS TOKEN 재발급 API [POST] /reissue
     *
     * @return ResponseEntity<Response<TokenDto>>
     */
    // Body
    @Operation(summary = "ACCESS TOKEN 재발급")
    @GetMapping("/reissue")
    public ResponseEntity<Response<TokenDto>> reissue(ClientType clientType) {
        log.info("[POST] /api/users/reissue");
        return authService.reissue(clientType);
    }

    /**
     * 이메일 인증 코드 보내기 API [POST] /email
     *
     * @return ResponseEntity<Response<EmailOutput>>
     */
    // Body
    @Operation(summary = "Eamil 인증 코드 발송")
    @PostMapping("/email/issue")
    public ResponseEntity<Response<IssueOutput>> sendMail(@RequestBody @Valid IssueInput emailInput) {
        log.info("[POST] /users/email");
        return authService.sendMail(emailInput);
    }

    /**
     * 이메일 인증 코드 확인 API [POST] /email/validation
     *
     * @return ResponseEntity<Response<ValidationDto>>
     */
    // Body
    @Operation(summary = "Eamil 인증 코드 확인")
    @PostMapping("/email/validation")
    public ResponseEntity<Response<ValidationDto>> checkMail(@RequestBody @Valid ValidationInput validationInput) {
        log.info("[POST] /email/validation");
        return authService.checkMail(validationInput);
    }

    /**
     * refresh token 유효성 검증 API [POST] /token/validation
     * @return ResponseEntity<Response<ValidationDto>>
     */
    // Body
    @Operation(summary = "마지막으로 로그인한 기기가 맞는지 체크")
    @GetMapping("/device")
    public ResponseEntity<Response<ValidationDto>>checkLastly(ClientType clientType) {
        log.info("[POST] /device");
        return authService.checkLastly(clientType);
    }

}
