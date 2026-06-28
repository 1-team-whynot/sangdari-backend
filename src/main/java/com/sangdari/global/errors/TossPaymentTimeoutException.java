package com.sangdari.global.errors;

public class TossPaymentTimeoutException extends RuntimeException {
    public TossPaymentTimeoutException(String message) {
        super(message);
    }
}
