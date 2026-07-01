package com.sangdari.domain.payment.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossConfirmResponse(

        String paymentKey,

        String orderId,

        String type,

        String method,

        String status,

        Long totalAmount,

        Long balanceAmount,

        String approvedAt,

        Receipt receipt

) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Receipt(
            String url
    ) {
    }
}