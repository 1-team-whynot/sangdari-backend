package com.sangdari.domain.payment.controllers;

import com.sangdari.domain.payment.requests.PaymentConfirmRequest;
import com.sangdari.domain.payment.requests.PaymentMockConfirmRequest;
import com.sangdari.domain.payment.requests.PaymentReadyRequest;
import com.sangdari.domain.payment.responses.PaymentConfirmResponse;
import com.sangdari.domain.payment.responses.PaymentReadyResponse;
import com.sangdari.domain.payment.services.PaymentService;
import com.sangdari.global.response.GlobalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/ready")
    public ResponseEntity<GlobalResponse<PaymentReadyResponse>> ready(
            @Valid @RequestBody PaymentReadyRequest request
    ) {
        PaymentReadyResponse response = paymentService.ready(request);

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
            @Valid @RequestBody PaymentMockConfirmRequest request
    ) {
        PaymentConfirmResponse response = paymentService.mockConfirm(request);

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
            @Valid @RequestBody PaymentConfirmRequest request
    ) {
        PaymentConfirmResponse response = paymentService.confirmTossPayment(request);

        return ResponseEntity.ok(
                GlobalResponse.<PaymentConfirmResponse>builder()
                        .code("00")
                        .message("PAYMENT_TOSS_CONFIRM_SUCCESS")
                        .data(response)
                        .build()
        );
    }
}