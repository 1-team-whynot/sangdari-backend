package com.sangdari.global.errors;

public class TossPaymentRejectedException extends RuntimeException {
    public TossPaymentRejectedException(String message) {
        super(message);
    }
}
