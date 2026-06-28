package com.sangdari.domain.payment.responses;


public record PaymentReadyResponse(
        Long paymentId,
        String orderId,
        String orderName,
        Long amount,
        String paymentType,
        String customerName,
        String customerEmail
) {
}
