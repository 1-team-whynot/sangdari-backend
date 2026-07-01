package com.sangdari.domain.payment.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PaymentReadyRequest(
    @NotNull(message = "예약한 아이디가 존재하지 않습니다.")
    Long reservationId,

    @NotBlank(message = "결제 상태가 올바르지 않습니다.")
    @Pattern(regexp = "^(DEPOSIT|BALANCE)$")
    String paymentType
) {
}
