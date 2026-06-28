package com.sangdari.global.exception;

import com.sangdari.global.exception.custom.*;
import com.sangdari.global.response.GlobalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse<Map<String, String>>> methodArgumentNotValidHandle(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "유효하지 않은 값입니다.",
                (existing, replacement) -> existing
            ));

        return ResponseEntity.status(400).body(
            GlobalResponse.<Map<String, String>>builder()
            .code("E10")
            .message("VALIDATION_FAILED")
            .data(errors)
            .build()
        );
    }

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

    @ExceptionHandler(AuthTokenExpiredException.class)
    public ResponseEntity<GlobalResponse<String>> authTokenExpiredHandle(
            AuthTokenExpiredException exception
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            GlobalResponse.<String>builder()
            .code("E22")
            .message("AUTH_TOKEN_EXPIRED")
            .data(exception.getMessage())
            .build()
        );
    }

    @ExceptionHandler(UserEmailDuplicatedException.class)
    public ResponseEntity<GlobalResponse<String>> userEmailDuplicatedHandle(
            UserEmailDuplicatedException exception
    ) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(GlobalResponse.<String>builder()
                .code("E30")
                .message("USER_EMAIL_DUPLICATED")
                .data(exception.getMessage())
                .build());
    }

    @ExceptionHandler(UserPhoneDuplicatedException.class)
    public ResponseEntity<GlobalResponse<String>> userPhoneDuplicatedException(
            UserPhoneDuplicatedException exception
    ) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(GlobalResponse.<String>builder()
                .code("E30")
                .message("USER_PHONE_DUPLICATED")
                .data(exception.getMessage())
                .build());
    }

    @ExceptionHandler(UserPasswordMismatchException.class)
    public ResponseEntity<GlobalResponse<String>> userPasswordMismatchHandle(
            UserPasswordMismatchException exception
    ) {
        return ResponseEntity
            .badRequest()
            .body(GlobalResponse.<String>builder()
                .code("E31")
                .message("USER_PASSWORD_MISMATCH")
                .data(exception.getMessage())
                .build()
            );
    }

    @ExceptionHandler(UserInvalidPasswordException.class)
    public ResponseEntity<GlobalResponse<String>> userInvalidPasswordHandle(
            UserInvalidPasswordException exception
    ) {
        return ResponseEntity
            .badRequest()
            .body(GlobalResponse.<String>builder()
                .code("E32")
                .message("USER_INVALID_PASSWORD")
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
