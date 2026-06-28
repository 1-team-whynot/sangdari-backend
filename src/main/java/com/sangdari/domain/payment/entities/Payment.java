package com.sangdari.domain.payment.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment {
    private Long paymentId;

    private Long reservationId;
    private Long userId;

    private String paymentKey;
    private String orderId;
    private String orderName;

    private String paymentType; // DEPOSIT, BALANCE, FULL_PAYMENT
    private String method;      // CARD, EASY_PAY, TRANSFER 등
    private String status;      // READY, DONE, CANCELED, ABORTED 등

    private Long totalAmount;
    private Long balanceAmount;
    private Long platformFee;

    private String lastTransactionKey;
    private Boolean isPartialCancelable;

    private String requestedAt;
    private String approvedAt;

    private String receiptUrl;
    private String failureCode;
    private String failureMessage;

    private String rawResponse;

    private String createdAt;
    private String updatedAt;
    private String deletedAt;
}
