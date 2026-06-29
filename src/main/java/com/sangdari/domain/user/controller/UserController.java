package com.sangdari.domain.user.controller;

import com.sangdari.domain.user.requests.UserPasswordChangeRequest;
import com.sangdari.domain.user.requests.UserUpdateRequest;
import com.sangdari.domain.user.requests.UserWithdrawRequest;
import com.sangdari.domain.user.responses.UserResponse;
import com.sangdari.domain.user.services.UserService;
import com.sangdari.global.response.GlobalResponse;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @GetMapping("/users/info")
    public ResponseEntity<GlobalResponse<UserResponse>> getMyInfo(
        @AuthenticationPrincipal Claims claims
    ) {
        // Token에 저장되어 있는 subject(사용자의 PK)를 파싱하여 활용합니다.
        Long userId = Long.parseLong(claims.getSubject());

        UserResponse userResponse = userService.index(userId);

        return ResponseEntity.ok(
            GlobalResponse.<UserResponse>builder()
                .code("00")
                .message("내 정보 조회 완료")
                .data(userResponse)
                .build()
        );
    }

    @PutMapping("/users/info-update")
    public ResponseEntity<GlobalResponse<UserResponse>> updateMyInfo(
        @AuthenticationPrincipal Claims claims,
        @Valid @RequestBody UserUpdateRequest request
    ) {
        Long userId = Long.parseLong(claims.getSubject());

        UserResponse response = userService.update(userId, request);

        return ResponseEntity.ok(
            GlobalResponse.<UserResponse>builder()
                .code("00")
                .message("정상 처리되었습니다.")
                .data(response)
                .build()
        );
    }

    @PutMapping("/users/password")
    public ResponseEntity<GlobalResponse<Void>> changePassword(
        @AuthenticationPrincipal Claims claims,
        @Valid @RequestBody UserPasswordChangeRequest request
    ) {
        Long userId = Long.parseLong(claims.getSubject());

        userService.changePassword(userId, request);
        return ResponseEntity.ok(
            GlobalResponse.<Void>builder()
                .code("00")
                .message("비밀번호가 변경되었습니다.")
                .build());
    }

    @DeleteMapping("/users/withdraw")
    public ResponseEntity<GlobalResponse<Void>> withdraw(
        @AuthenticationPrincipal Claims claims,
        @Valid @RequestBody UserWithdrawRequest request
    ) {
        Long userId = Long.parseLong(claims.getSubject());

        userService.withdraw(userId, request);

        return ResponseEntity.ok(
            GlobalResponse.<Void>builder()
                .code("00")
                .message("회원 탈퇴가 완료되었습니다.")
                .data(null)
                .build()
        );
    }
}