package com.sangdari.global.exception.custom;

public class PaymentOrderIdDuplicatedException extends RuntimeException {
    public PaymentOrderIdDuplicatedException(String message) {
        super(message);
    }
}
