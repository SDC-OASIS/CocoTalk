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

    @PostMapping("/signin")
    public ResponseEntity<Response<TokenDto>> signin(@RequestBody @Valid SigninInput signinInput) {
//        String token = authService.login(loginRequest);
//        LoginResponse loginResponse = LoginResponse.toDto(token);
        return authService.signin(signinInput);
    }

    /**
     * 회원가입 API [POST] /api/users/signup
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
     * ACESS TOKEN 재발급 API [POST] /api/users/reissue
     *
     * @return ResponseEntity<Response<ReissueOutput>>
     */
    // Body
    @GetMapping("/reissue")
    public ResponseEntity<Response<TokenDto>> reissue() {
        log.info("[POST] /api/users/reissue");
        return authService.reissue();
    }

}
