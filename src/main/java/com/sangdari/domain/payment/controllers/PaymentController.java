package com.sangdari.domain.payment.controllers;

import com.sangdari.domain.payment.requests.PaymentConfirmRequest;
import com.sangdari.domain.payment.requests.PaymentMockConfirmRequest;
import com.sangdari.domain.payment.requests.PaymentReadyRequest;
import com.sangdari.domain.payment.responses.PaymentConfirmResponse;
import com.sangdari.domain.payment.responses.PaymentReadyResponse;
import com.sangdari.domain.payment.services.PaymentService;
import com.sangdari.global.exception.custom.AuthLoginRequiredException;
import com.sangdari.global.response.GlobalResponse;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/ready")
    public ResponseEntity<GlobalResponse<PaymentReadyResponse>> ready(
            Authentication authentication,
            @Valid @RequestBody PaymentReadyRequest request
    ) {
        PaymentReadyResponse response = paymentService.ready(getLoginUserId(authentication), request);

        return ResponseEntity.ok(
                GlobalResponse.<PaymentReadyResponse>builder()
                        .code("00")
                        .message("PAYMENT_READY_SUCCESS")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/mock-confirm")
    public ResponseEntity<GlobalResponse<PaymentConfirmResponse>> mockConfirm(
            Authentication authentication,
            @Valid @RequestBody PaymentMockConfirmRequest request
    ) {
        PaymentConfirmResponse response = paymentService.mockConfirm(getLoginUserId(authentication), request);

        return ResponseEntity.ok(
                GlobalResponse.<PaymentConfirmResponse>builder()
                        .code("00")
                        .message("PAYMENT_MOCK_CONFIRM_SUCCESS")
                        .data(response)
                        .build()
        );
    }
    @PostMapping("/confirm")
    public ResponseEntity<GlobalResponse<PaymentConfirmResponse>> confirm(
            Authentication authentication,
            @Valid @RequestBody PaymentConfirmRequest request
    ) {
        PaymentConfirmResponse response = paymentService.confirmTossPayment(getLoginUserId(authentication), request);

        return ResponseEntity.ok(
                GlobalResponse.<PaymentConfirmResponse>builder()
                        .code("00")
                        .message("PAYMENT_TOSS_CONFIRM_SUCCESS")
                        .data(response)
                        .build()
        );
    }

    private Long getLoginUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Claims claims)) {
            throw new AuthLoginRequiredException();
        }

        return Long.parseLong(claims.getSubject());
    }
}
