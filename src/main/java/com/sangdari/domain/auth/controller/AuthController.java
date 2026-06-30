package com.sangdari.domain.auth.controller;

import com.sangdari.domain.auth.requests.AuthPasswordResetRequest;
import com.sangdari.domain.auth.requests.AuthUserVerifyRequest;
import com.sangdari.domain.auth.requests.LoginRequest;
import com.sangdari.domain.auth.requests.SignupRequest;
import com.sangdari.domain.auth.responses.AuthResponse;
import com.sangdari.domain.auth.services.AuthService;
import com.sangdari.global.response.GlobalResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<GlobalResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest
            , HttpServletResponse response
    ) {
        return ResponseEntity.status(200).body(
            GlobalResponse.<AuthResponse>builder()
                .code("00")
                .message("로그인 완료")
                .data(authService.login(response, loginRequest))
                .build()
        );
    }

    @PostMapping("/reissue-token")
    public ResponseEntity<GlobalResponse<AuthResponse>> reissue(
            HttpServletRequest request
            , HttpServletResponse response
    ) {
        return ResponseEntity.status(200).body(
            GlobalResponse.<AuthResponse>builder()
            .code("00")
            .message("토큰 재발급 완료")
            .data(authService.reissue(request, response))
            .build()
        );
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<GlobalResponse<String>> logout(
            HttpServletResponse response
            , @AuthenticationPrincipal Claims claims // JWT 필터를 통과한 사용자 클레임 획득
    ) {
        // claims.getSubject()에는 토큰 발행 시 담았던 유저 식별자(ID)가 들어있습니다.
        authService.logout(response, Long.parseLong(claims.getSubject()));

        return ResponseEntity.status(200).body(
                GlobalResponse.<String>builder()
                        .code("00")
                        .message("로그아웃 완료")
                        .build()
        );
    }

    @PostMapping("/users/signup")
    public ResponseEntity<GlobalResponse<String>> signup(
            @Valid @RequestBody SignupRequest signupRequest
    ) {
        authService.signup(signupRequest);

        return ResponseEntity.status(201).body(
            GlobalResponse.<String>builder()
                .code("00")
                .message("회원가입 완료")
                .build()
        );
    }

    @GetMapping("/email-check")
    public ResponseEntity<GlobalResponse<Boolean>> checkEmail(
        @RequestParam String email
    ) {
        return ResponseEntity.status(200).body(
            GlobalResponse.<Boolean>builder()
                .code("00")
                .message("이메일 체크 완료")
                .data(authService.checkEmail(email))
                .build()
        );
    }

    @PutMapping("/find-password")
    public ResponseEntity<GlobalResponse<Void>> resetPassword(
            @Valid @RequestBody AuthPasswordResetRequest request
    ) {
        authService.resetPassword(request);
        return ResponseEntity.ok(
            GlobalResponse.<Void>builder()
                .code("00")
                .message("비밀번호가 재설정되었습니다.")
                .build());
    }

    @PostMapping("/auth/verify-user")
    public ResponseEntity<GlobalResponse<Void>> verifyUser(
            @Valid @RequestBody AuthUserVerifyRequest request
    ) {
        authService.verifyUser(request);
        return ResponseEntity.ok(
            GlobalResponse.<Void>builder()
                .code("00")
                .message("회원 정보가 확인되었습니다.")
                .build());
    }
}
