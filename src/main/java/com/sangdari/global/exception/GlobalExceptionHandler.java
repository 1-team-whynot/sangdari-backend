package com.sangdari.global.exception;

import com.sangdari.global.exception.custom.*;
import com.sangdari.global.responses.GlobalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================
    // E40 체크리스트/업체 조회 관련
    // =========================

    // E40: CHECKLIST_REQUIRED_MISSING
    @ExceptionHandler(ChecklistRequiredMissingException.class)
    public ResponseEntity<GlobalResponse<List<String>>> checklistRequiredMissingHandle(ChecklistRequiredMissingException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GlobalResponse.<List<String>>builder()
                        .code("E40")
                        .message("CHECKLIST_REQUIRED_MISSING")
                        .data(List.of(e.getMessage()))
                        .build());
    }

    // E41: CHECKLIST_DATE_TOO_SOON
    @ExceptionHandler(ChecklistDateTooSoonException.class)
    public ResponseEntity<GlobalResponse<List<String>>> checklistDateTooSoonHandle(ChecklistDateTooSoonException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(GlobalResponse.<List<String>>builder()
                        .code("E41")
                        .message("CHECKLIST_DATE_TOO_SOON")
                        .data(List.of(e.getMessage()))
                        .build());
    }

    // E42: CHECKLIST_INVALID_FORMAT
    @ExceptionHandler(ChecklistInvalidFormatException.class)
    public ResponseEntity<GlobalResponse<List<String>>> checklistInvalidFormatHandle(ChecklistInvalidFormatException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GlobalResponse.<List<String>>builder()
                        .code("E42")
                        .message("CHECKLIST_INVALID_FORMAT")
                        .data(List.of(e.getMessage()))
                        .build());
    }

    // E43: STORE_NOT_FOUND
    @ExceptionHandler(StoreNotFoundException.class)
    public ResponseEntity<GlobalResponse<List<String>>> storeNotFoundHandle(StoreNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(GlobalResponse.<List<String>>builder()
                        .code("E43")
                        .message("STORE_NOT_FOUND")
                        .data(List.of(e.getMessage()))
                        .build());
    }

    // E44: STORE_MENU_REQUIRED
    @ExceptionHandler(StoreMenuRequiredException.class)
    public ResponseEntity<GlobalResponse<List<String>>> storeMenuRequiredHandle(StoreMenuRequiredException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GlobalResponse.<List<String>>builder()
                        .code("E44")
                        .message("STORE_MENU_REQUIRED")
                        .data(List.of(e.getMessage()))
                        .build());
    }

    // E45: STORE_MENU_INVALID
    @ExceptionHandler(StoreMenuInvalidException.class)
    public ResponseEntity<GlobalResponse<List<String>>> storeMenuInvalidHandle(StoreMenuInvalidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GlobalResponse.<List<String>>builder()
                        .code("E45")
                        .message("STORE_MENU_INVALID")
                        .data(List.of(e.getMessage()))
                        .build());
    }
}