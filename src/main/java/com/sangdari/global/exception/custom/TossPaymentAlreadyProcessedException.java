package com.sangdari.global.exception.custom;

public class TossPaymentAlreadyProcessedException extends RuntimeException {
    public TossPaymentAlreadyProcessedException(String message) {
        super(message);
    }
}
