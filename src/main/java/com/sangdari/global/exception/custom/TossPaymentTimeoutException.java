package com.sangdari.global.exception.custom;

public class TossPaymentTimeoutException extends RuntimeException {
    public TossPaymentTimeoutException(String message) {
        super(message);
    }
}
