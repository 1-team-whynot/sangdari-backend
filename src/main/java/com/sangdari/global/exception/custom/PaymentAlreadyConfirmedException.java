package com.sangdari.global.exception.custom;

public class PaymentAlreadyConfirmedException extends RuntimeException {
    public PaymentAlreadyConfirmedException(String message) {
        super(message);
    }
}
