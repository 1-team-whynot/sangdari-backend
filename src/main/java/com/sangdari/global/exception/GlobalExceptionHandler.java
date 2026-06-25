package com.sangdari.global.exception;

import com.sangdari.global.exception.custom.AuthLoginFailedException;
import com.sangdari.global.exception.custom.AuthLoginRequiredException;
import com.sangdari.global.exception.custom.InvalidTokenException;
import com.sangdari.global.response.GlobalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthLoginRequiredException.class)
    public ResponseEntity<GlobalResponse<String>> authLoginRequiredHandle(
            AuthLoginRequiredException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(GlobalResponse.<String>builder()
                        .code("E20")
                        .message("AUTH_LOGIN_REQUIRED")
                        .data(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(AuthLoginFailedException.class)
    public ResponseEntity<GlobalResponse<String>> authLoginFailedHandle(
            AuthLoginFailedException exception
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            GlobalResponse.<String>builder()
            .code("E21")
            .message("AUTH_LOGIN_FAILED")
            .data(exception.getMessage())
            .build()
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<GlobalResponse<String>> invalidTokenHandle(
            AuthLoginFailedException exception
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            GlobalResponse.<String>builder()
            .code("E22")
            .message("AUTH_TOKEN_EXPIRED")
            .data(exception.getMessage())
            .build()
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponse<String>> othersHandle(Exception e) {
        log.error("시스템 에러: {}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));

        return ResponseEntity.status(500).body(
                GlobalResponse.<String>builder()
                        .code("E99")
                        .message("시스템 에러")
                        .data("현재 서비스 이용이 불가합니다. 잠시 후 다시 시도해 주십시오.")
                        .build()
        );
    }
}
