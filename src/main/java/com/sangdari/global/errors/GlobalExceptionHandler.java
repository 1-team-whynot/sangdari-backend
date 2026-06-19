package com.sangdari.global.errors;

import com.sangdari.global.errors.custom.DuplicatedUserException;
import com.sangdari.global.errors.custom.InvalidTokenException;
import com.sangdari.global.errors.custom.NotRegisteredException;
import com.sangdari.global.responses.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotRegisteredException.class)
    public ResponseEntity<BaseResponse<String>> notRegisteredHandle(NotRegisteredException e) {
        return ResponseEntity.status(401).body(
                BaseResponse.<String>builder()
                        .code("E01")
                        .message("REGISTRATION_ERROR")
                        .data(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<String>> badCredentialsExceptionHandle(BadCredentialsException e) {
        return ResponseEntity.status(401).body(
                BaseResponse.<String>builder()
                        .code("E02")
                        .message("LOGIN_ERROR")
                        .data(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<BaseResponse<String>> invalidTokenHandle(InvalidTokenException e) {
        return ResponseEntity.status(401).body(
                BaseResponse.<String>builder()
                        .code("E04")
                        .message("INVALID_TOKEN_ERROR")
                        .data(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(DuplicatedUserException.class)
    public ResponseEntity<BaseResponse<String>> duplicatedUserHandle(DuplicatedUserException e) {
        return ResponseEntity.status(409).body(
                BaseResponse.<String>builder()
                        .code("E11")
                        .message("DUPLICATED_USER_ERROR")
                        .data(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<String>> methodArgumentTypeMismatchHandle(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(400).body(
                BaseResponse.<String>builder()
                        .code("E21")
                        .message("요청 파라미터에 이상이 있습니다.")
                        .data(String.format("%s : 필드를 확인해 주세요.", e.getName()))
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> methodArgumentNotValidHandle(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField, // 필드명
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "유효하지 않은 값입니다.",
                        (existing, replacement) -> existing // 중복 필드가 있을 경우 기존 값 유지
                ));

        return ResponseEntity.status(400).body(
                BaseResponse.<Map<String, String>>builder()
                        .code("E21")
                        .message("요청 파라미터에 이상이 있습니다.")
                        .data(errors)
                        .build()
        );
    }
}
