package com.sangdari.global.exception.custom;

public class PaymentCancelNotSupportedException extends RuntimeException {
    public PaymentCancelNotSupportedException(String message) {
        super(message);
    }
}
