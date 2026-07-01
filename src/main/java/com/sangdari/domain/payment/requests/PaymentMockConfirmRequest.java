package com.sangdari.domain.payment.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentMockConfirmRequest(
        @NotBlank(message = "주문번호가 필요합니다.")
        String orderId,

        @NotNull(message = "결제 금액이 필요합니다.")
        @Positive(message = "결제 금액은 0보다 커야 합니다.")
        Long amount
) {
}