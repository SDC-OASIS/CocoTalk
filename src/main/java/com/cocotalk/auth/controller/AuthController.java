package com.cocotalk.auth.controller;

import com.cocotalk.auth.dto.common.ClientType;
import com.cocotalk.auth.dto.common.TokenDto;
import com.cocotalk.auth.dto.email.issue.IssueInput;
import com.cocotalk.auth.service.AuthService;
import com.cocotalk.auth.dto.email.issue.IssueOutput;
import com.cocotalk.auth.dto.email.validation.ValidationInput;
import com.cocotalk.auth.dto.common.ValidationDto;
import com.cocotalk.auth.dto.signin.SigninInput;
import com.cocotalk.auth.dto.signup.SignupInput;
import com.cocotalk.auth.dto.signup.SignupOutput;
import com.cocotalk.auth.dto.common.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 API [POST] /api/auth/signin
     *
     * @return ResponseEntity<Response<TokenDto>>
     */
    @ApiOperation(value = "로그인")
    @PostMapping("/signin")
    public ResponseEntity<Response<TokenDto>> signin(ClientType clientType, @RequestBody @Valid SigninInput signinInput) {
        return authService.signin(clientType, signinInput);
    }

    /**
     * 로그아웃 API [POST] /api/auth/signout
     *
     * @return ResponseEntity<Response<Object>>
     */
    @ApiOperation(value = "로그아웃")
    @GetMapping("/signout")
    public ResponseEntity<Response<Object>> signout(ClientType clientType) {
        return authService.signout(clientType);
    }

    /**
     * 회원가입 API [POST] /api/auth/signup
     *
     * @return ResponseEntity<Response<SignUpOutput>>
     */
    // Body
    @ApiOperation(value = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<Response<SignupOutput>> signup(@RequestBody @Valid SignupInput signUpInput) {
        log.info("[POST] /api/users/signup");
        return authService.signup(signUpInput);
    }

    /**
     * ACCESS TOKEN 재발급 API [POST] /api/auth/reissue
     *
     * @return ResponseEntity<Response<TokenDto>>
     */
    // Body
    @ApiOperation(value = "ACCESS TOKEN 재발급")
    @GetMapping("/reissue")
    public ResponseEntity<Response<TokenDto>> reissue(ClientType clientType) {
        log.info("[POST] /api/users/reissue");
        return authService.reissue(clientType);
    }

    /**
     * 이메일 인증 코드 보내기 API [POST] /api/users/email
     *
     * @return ResponseEntity<Response<EmailOutput>>
     */
    // Body
    @ApiOperation(value = "Eamil 인증 코드 발송")
    @PostMapping("/email/issue")
    public ResponseEntity<Response<IssueOutput>> sendMail(@RequestBody @Valid IssueInput emailInput) {
        log.info("[POST] /users/email");
        return authService.sendMail(emailInput);
    }

    /**
     * 이메일 인증 코드 확인 API [POST] /api/auth/email/validation
     *
     * @return ResponseEntity<Response<ValidationDto>>
     */
    // Body
    @ApiOperation(value = "Eamil 인증 코드 확인")
    @PostMapping("/email/validation")
    public ResponseEntity<Response<ValidationDto>> checkMail(@RequestBody @Valid ValidationInput validationInput) {
        log.info("[POST] /email/validation");
        return authService.checkMail(validationInput);
    }

    /**
     * refresh token 유효성 검증 API [POST] /api/auth/token/validation
     * @return ResponseEntity<Response<ValidationDto>>
     */
    // Body
    @ApiOperation(value = "refresh token이 유효한지 확인")
    @GetMapping("/token/validation")
    public ResponseEntity<Response<ValidationDto>>checkRefershToken(ClientType clientType) {
        log.info("[POST] /token/validation");
        return authService.isValidRefreshToken(clientType);
    }

}
