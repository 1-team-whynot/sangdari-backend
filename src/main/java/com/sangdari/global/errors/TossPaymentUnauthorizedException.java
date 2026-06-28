package com.sangdari.global.errors;

public class TossPaymentUnauthorizedException extends RuntimeException {
    public TossPaymentUnauthorizedException(String message) {
        super(message);
    }
}
