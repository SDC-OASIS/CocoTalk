package com.cocotalk.controller;

import com.cocotalk.dto.common.TokenDto;
import com.cocotalk.dto.signin.SigninInput;
import com.cocotalk.dto.signup.SignupInput;
import com.cocotalk.dto.signup.SignupOutput;
import com.cocotalk.response.Response;
import com.cocotalk.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    @PostMapping("/signin")
    public ResponseEntity<Response<TokenDto>> signin(@RequestBody @Valid SigninInput signinInput) {
        return authService.signin(signinInput);
    }

    /**
     * 로그아웃 API [POST] /api/auth/signout
     *
     * @return ResponseEntity<Response<Object>>
     */
    @GetMapping("/signout")
    public ResponseEntity<Response<Object>> signout() {
        return authService.signout();
    }

    /**
     * 회원가입 API [POST] /api/auth/signup
     *
     * @return ResponseEntity<Response<SignUpOutput>>
     */
    // Body
    @PostMapping("/signup")
    public ResponseEntity<Response<SignupOutput>> signup(@RequestBody SignupInput signUpInput) {
        log.info("[POST] /api/users/signup");
        return authService.signup(signUpInput);
    }


    /**
     * ACESS TOKEN 재발급 API [POST] /api/auth/reissue
     *
     * @return ResponseEntity<Response<TokenDto>>
     */
    // Body
    @GetMapping("/reissue")
    public ResponseEntity<Response<TokenDto>> reissue() {
        log.info("[POST] /api/users/reissue");
        return authService.reissue();
    }

}
