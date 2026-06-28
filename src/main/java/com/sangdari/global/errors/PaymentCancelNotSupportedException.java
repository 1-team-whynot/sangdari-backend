package com.sangdari.global.errors;

public class PaymentCancelNotSupportedException extends RuntimeException {
    public PaymentCancelNotSupportedException(String message) {
        super(message);
    }
}
