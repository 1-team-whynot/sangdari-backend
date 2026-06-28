package com.sangdari.global;

import com.sangdari.global.errors.*;
import com.sangdari.global.responses.GlobalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.util.LinkedHashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        // =========================
        // E10 요청 검증 관련
        // =========================

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<GlobalResponse<Map<String, String>>> methodArgumentNotValidHandle(
                MethodArgumentNotValidException e
        ) {
            Map<String, String> errors = new LinkedHashMap<>();

            for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    GlobalResponse.<Map<String, String>>builder()
                            .code("E10")
                            .message("VALIDATION_FAILED")
                            .data(errors)
                            .build()
            );
        }

        @ExceptionHandler(BindException.class)
        public ResponseEntity<GlobalResponse<Map<String, String>>> bindHandle(
                BindException e
        ) {
            Map<String, String> errors = new LinkedHashMap<>();

            for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    GlobalResponse.<Map<String, String>>builder()
                            .code("E11")
                            .message("BINDING_FAILED")
                            .data(errors)
                            .build()
            );
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<GlobalResponse<String>> httpMessageNotReadableHandle(
                HttpMessageNotReadableException e
        ) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "E12",
                    "INVALID_REQUEST_BODY",
                    "요청 본문 형식이 올바르지 않습니다."
            );
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<GlobalResponse<String>> missingServletRequestParameterHandle(
                MissingServletRequestParameterException e
        ) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "E13",
                    "MISSING_REQUEST_PARAMETER",
                    "필수 요청 파라미터가 누락되었습니다. (" + e.getParameterName() + ")"
            );
        }

        // =========================
        // E50 예약 관련
        // =========================

        @ExceptionHandler(ReservationNotFoundException.class)
        public ResponseEntity<GlobalResponse<String>> reservationNotFoundHandle(
                ReservationNotFoundException e
        ) {
            return error(
                    HttpStatus.NOT_FOUND,
                    "E50",
                    "RESERVATION_NOT_FOUND",
                    e.getMessage()
            );
        }

        @ExceptionHandler(ReservationInvalidStatusException.class)
        public ResponseEntity<GlobalResponse<String>> reservationInvalidStatusHandle(
                ReservationInvalidStatusException e
        ) {
            return error(
                    HttpStatus.valueOf(422),
                    "E51",
                    "RESERVATION_INVALID_STATUS",
                    e.getMessage()
            );
        }

        @ExceptionHandler(ReservationPaymentInfoNotFoundException.class)
        public ResponseEntity<GlobalResponse<String>> reservationPaymentInfoNotFoundHandle(
                ReservationPaymentInfoNotFoundException e
        ) {
            return error(
                    HttpStatus.NOT_FOUND,
                    "E52",
                    "RESERVATION_PAYMENT_INFO_NOT_FOUND",
                    e.getMessage()
            );
        }

        @ExceptionHandler(ReservationOwnerMismatchException.class)
        public ResponseEntity<GlobalResponse<String>> reservationOwnerMismatchHandle(
                ReservationOwnerMismatchException e
        ) {
            return error(
                    HttpStatus.FORBIDDEN,
                    "E53",
                    "RESERVATION_OWNER_MISMATCH",
                    e.getMessage()
            );
        }

        @ExceptionHandler(ReservationAlreadyCompletedException.class)
        public ResponseEntity<GlobalResponse<String>> reservationAlreadyCompletedHandle(
                ReservationAlreadyCompletedException e
        ) {
            return error(
                    HttpStatus.CONFLICT,
                    "E54",
                    "RESERVATION_ALREADY_COMPLETED",
                    e.getMessage()
            );
        }

        @ExceptionHandler(ReservationAlreadyCanceledException.class)
        public ResponseEntity<GlobalResponse<String>> reservationAlreadyCanceledHandle(
                ReservationAlreadyCanceledException e
        ) {
            return error(
                    HttpStatus.CONFLICT,
                    "E55",
                    "RESERVATION_ALREADY_CANCELED",
                    e.getMessage()
            );
        }

        // =========================
        // E60 결제 내부 관련
        // =========================

        @ExceptionHandler(PaymentNotFoundException.class)
        public ResponseEntity<GlobalResponse<String>> paymentNotFoundHandle(
                PaymentNotFoundException e
        ) {
            return error(
                    HttpStatus.NOT_FOUND,
                    "E60",
                    "PAYMENT_NOT_FOUND",
                    e.getMessage()
            );
        }

        @ExceptionHandler(PaymentDuplicatedException.class)
        public ResponseEntity<GlobalResponse<String>> paymentDuplicatedHandle(
                PaymentDuplicatedException e
        ) {
            return error(
                    HttpStatus.CONFLICT,
                    "E61",
                    "PAYMENT_DUPLICATED",
                    e.getMessage()
            );
        }

        @ExceptionHandler(PaymentInvalidStatusException.class)
        public ResponseEntity<GlobalResponse<String>> paymentInvalidStatusHandle(
                PaymentInvalidStatusException e
        ) {
            return error(
                    HttpStatus.valueOf(422),
                    "E62",
                    "PAYMENT_INVALID_STATUS",
                    e.getMessage()
            );
        }

        @ExceptionHandler(PaymentAmountMismatchException.class)
        public ResponseEntity<GlobalResponse<String>> paymentAmountMismatchHandle(
                PaymentAmountMismatchException e
        ) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "E63",
                    "PAYMENT_AMOUNT_MISMATCH",
                    e.getMessage()
            );
        }

        @ExceptionHandler(PaymentFailedException.class)
        public ResponseEntity<GlobalResponse<String>> paymentFailedHandle(
                PaymentFailedException e
        ) {
            return error(
                    HttpStatus.valueOf(422),
                    "E64",
                    "PAYMENT_FAILED",
                    e.getMessage()
            );
        }

        @ExceptionHandler(PaymentTypeInvalidException.class)
        public ResponseEntity<GlobalResponse<String>> paymentTypeInvalidHandle(
                PaymentTypeInvalidException e
        ) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "E65",
                    "PAYMENT_TYPE_INVALID",
                    e.getMessage()
            );
        }

        @ExceptionHandler(PaymentAmountInvalidException.class)
        public ResponseEntity<GlobalResponse<String>> paymentAmountInvalidHandle(
                PaymentAmountInvalidException e
        ) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "E66",
                    "PAYMENT_AMOUNT_INVALID",
                    e.getMessage()
            );
        }

        @ExceptionHandler(PaymentOrderIdDuplicatedException.class)
        public ResponseEntity<GlobalResponse<String>> paymentOrderIdDuplicatedHandle(
                PaymentOrderIdDuplicatedException e
        ) {
            return error(
                    HttpStatus.CONFLICT,
                    "E67",
                    "PAYMENT_ORDER_ID_DUPLICATED",
                    e.getMessage()
            );
        }

        @ExceptionHandler(PaymentAlreadyConfirmedException.class)
        public ResponseEntity<GlobalResponse<String>> paymentAlreadyConfirmedHandle(
                PaymentAlreadyConfirmedException e
        ) {
            return error(
                    HttpStatus.CONFLICT,
                    "E68",
                    "PAYMENT_ALREADY_CONFIRMED",
                    e.getMessage()
            );
        }

        @ExceptionHandler(PaymentCancelNotSupportedException.class)
        public ResponseEntity<GlobalResponse<String>> paymentCancelNotSupportedHandle(
                PaymentCancelNotSupportedException e
        ) {
            return error(
                    HttpStatus.valueOf(422),
                    "E69",
                    "PAYMENT_CANCEL_NOT_SUPPORTED",
                    e.getMessage()
            );
        }

        // =========================
        // E70 TossPayments 관련
        // =========================

        @ExceptionHandler(TossPaymentConfirmFailedException.class)
        public ResponseEntity<GlobalResponse<String>> tossPaymentConfirmFailedHandle(
                TossPaymentConfirmFailedException e
        ) {
            return error(
                    HttpStatus.BAD_GATEWAY,
                    "E70",
                    "TOSS_PAYMENT_CONFIRM_FAILED",
                    e.getMessage()
            );
        }

        @ExceptionHandler(TossPaymentApiException.class)
        public ResponseEntity<GlobalResponse<String>> tossPaymentApiHandle(
                TossPaymentApiException e
        ) {
            return error(
                    HttpStatus.BAD_GATEWAY,
                    "E71",
                    "TOSS_PAYMENT_API_ERROR",
                    e.getMessage()
            );
        }

        @ExceptionHandler(TossPaymentTimeoutException.class)
        public ResponseEntity<GlobalResponse<String>> tossPaymentTimeoutHandle(
                TossPaymentTimeoutException e
        ) {
            return error(
                    HttpStatus.GATEWAY_TIMEOUT,
                    "E72",
                    "TOSS_PAYMENT_TIMEOUT",
                    e.getMessage()
            );
        }

        @ExceptionHandler(TossPaymentInvalidResponseException.class)
        public ResponseEntity<GlobalResponse<String>> tossPaymentInvalidResponseHandle(
                TossPaymentInvalidResponseException e
        ) {
            return error(
                    HttpStatus.BAD_GATEWAY,
                    "E73",
                    "TOSS_PAYMENT_INVALID_RESPONSE",
                    e.getMessage()
            );
        }

        @ExceptionHandler(TossPaymentUnauthorizedException.class)
        public ResponseEntity<GlobalResponse<String>> tossPaymentUnauthorizedHandle(
                TossPaymentUnauthorizedException e
        ) {
            return error(
                    HttpStatus.BAD_GATEWAY,
                    "E74",
                    "TOSS_PAYMENT_UNAUTHORIZED",
                    e.getMessage()
            );
        }

        @ExceptionHandler(TossPaymentRejectedException.class)
        public ResponseEntity<GlobalResponse<String>> tossPaymentRejectedHandle(
                TossPaymentRejectedException e
        ) {
            return error(
                    HttpStatus.valueOf(422),
                    "E75",
                    "TOSS_PAYMENT_REJECTED",
                    e.getMessage()
            );
        }

        @ExceptionHandler(TossPaymentAlreadyProcessedException.class)
        public ResponseEntity<GlobalResponse<String>> tossPaymentAlreadyProcessedHandle(
                TossPaymentAlreadyProcessedException e
        ) {
            return error(
                    HttpStatus.CONFLICT,
                    "E76",
                    "TOSS_PAYMENT_ALREADY_PROCESSED",
                    e.getMessage()
            );
        }

        @ExceptionHandler(TossPaymentCanceledException.class)
        public ResponseEntity<GlobalResponse<String>> tossPaymentCanceledHandle(
                TossPaymentCanceledException e
        ) {
            return error(
                    HttpStatus.valueOf(422),
                    "E77",
                    "TOSS_PAYMENT_CANCELED",
                    e.getMessage()
            );
        }

        @ExceptionHandler(TossPaymentNetworkException.class)
        public ResponseEntity<GlobalResponse<String>> tossPaymentNetworkHandle(
                TossPaymentNetworkException e
        ) {
            return error(
                    HttpStatus.BAD_GATEWAY,
                    "E78",
                    "TOSS_PAYMENT_NETWORK_ERROR",
                    e.getMessage()
            );
        }

        // =========================
        // E90 시스템 관련
        // =========================

        @ExceptionHandler(JsonProcessingException.class)
        public ResponseEntity<GlobalResponse<String>> jsonProcessingHandle(
                JsonProcessingException e
        ) {
            log.error("JSON 처리 실패", e);

            return error(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "E90",
                    "JSON_PROCESSING_FAILED",
                    "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            );
        }

        @ExceptionHandler(DatabaseOperationFailedException.class)
        public ResponseEntity<GlobalResponse<String>> databaseOperationFailedHandle(
                DatabaseOperationFailedException e
        ) {
            log.error("DB 처리 실패", e);

            return error(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "E91",
                    "DATABASE_OPERATION_FAILED",
                    e.getMessage()
            );
        }

        @ExceptionHandler(ExternalApiException.class)
        public ResponseEntity<GlobalResponse<String>> externalApiHandle(
                ExternalApiException e
        ) {
            log.error("외부 API 처리 실패", e);

            return error(
                    HttpStatus.BAD_GATEWAY,
                    "E92",
                    "EXTERNAL_API_FAILED",
                    e.getMessage()
            );
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<GlobalResponse<String>> illegalArgumentHandle(
                IllegalArgumentException e
        ) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "E93",
                    "INVALID_ARGUMENT",
                    e.getMessage()
            );
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<GlobalResponse<String>> exceptionHandle(
                Exception e
        ) {
            log.error("예상하지 못한 서버 오류", e);

            return error(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "E99",
                    "SYSTEM_INTERNAL_ERROR",
                    "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            );
        }

        // =========================
        // 공통 응답 생성 메서드
        // =========================

        private ResponseEntity<GlobalResponse<String>> error(
                HttpStatus status,
                String code,
                String message,
                String data
        ) {
            return ResponseEntity.status(status).body(
                    GlobalResponse.<String>builder()
                            .code(code)
                            .message(message)
                            .data(data)
                            .build()
            );
        }
}

