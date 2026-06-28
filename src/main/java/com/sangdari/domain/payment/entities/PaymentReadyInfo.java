package com.sangdari.domain.payment.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentReadyInfo {

    private Long reservationId;
    private Long userId;

    private String reservationStatus;

    private String customerName;
    private String customerEmail;

    private Long quotedPrice;
    private Long discountPrice;

    private Long payableAmount;
}
