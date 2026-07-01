package com.sangdari.domain.payment.responses;

import com.sangdari.domain.payment.entities.Payment;

public record PaymentConfirmResponse(
        Long paymentId,
        Long reservationId,
        String orderId,
        String paymentKey,
        String paymentType,
        String method,
        String status,
        Long totalAmount,
        Long balanceAmount,
        String receiptUrl,
        String approvedAt
) {
    public static PaymentConfirmResponse from(Payment payment) {
        return new PaymentConfirmResponse(
                payment.getPaymentId(),
                payment.getReservationId(),
                payment.getOrderId(),
                payment.getPaymentKey(),
                payment.getPaymentType(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getTotalAmount(),
                payment.getBalanceAmount(),
                payment.getReceiptUrl(),
                payment.getApprovedAt()
        );
    }
}