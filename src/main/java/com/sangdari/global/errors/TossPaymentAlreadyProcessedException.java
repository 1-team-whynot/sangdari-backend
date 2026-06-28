package com.sangdari.global.errors;

public class TossPaymentAlreadyProcessedException extends RuntimeException {
    public TossPaymentAlreadyProcessedException(String message) {
        super(message);
    }
}
