package com.sangdari.global.exception.custom;

public class TossPaymentUnauthorizedException extends RuntimeException {
    public TossPaymentUnauthorizedException(String message) {
        super(message);
    }
}
