package com.sangdari.domain.auth.controllers;

import com.sangdari.domain.auth.requests.LoginRequest;
import com.sangdari.domain.auth.requests.RegistrationRequest;
import com.sangdari.domain.auth.responses.AuthResponse;
import com.sangdari.domain.auth.services.AuthService;
import com.sangdari.global.responses.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/users/registration")
    public ResponseEntity<BaseResponse<String>> registration(
            @Valid @RequestBody RegistrationRequest registrationRequest
    ) {
        authService.registration(registrationRequest);

        return ResponseEntity.status(200).body(
                BaseResponse.<String>builder()
                        .code("00")
                        .message("회원가입 성공")
                        .build()
        );
    }

    @PostMapping("/users/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest
            , HttpServletResponse response
    ) {
        return ResponseEntity.status(200).body(
                BaseResponse.<AuthResponse>builder()
                        .code("00")
                        .message("정상 처리")
                        .data(authService.login(loginRequest, response))
                        .build()
        );
    }
}
