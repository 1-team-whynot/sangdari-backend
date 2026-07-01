package com.sangdari.domain.payment.responses;


import java.util.List;

public record PaymentReadyResponse(
        Long paymentId,
        String orderId,
        String orderName,
        Long amount,
        String paymentType,
        String customerName,
        String customerEmail,
        Long reservationId,
        String storeName,
        String eventStartDate,
        String eventEndDate,
        String eventLocation,
        Long estimateAmount,
        Long payableAmount,
        List<PaymentReadyMenuResponse> menus
) {
}
