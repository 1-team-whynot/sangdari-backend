package com.sangdari.global.errors;

public class PaymentAlreadyConfirmedException extends RuntimeException {
    public PaymentAlreadyConfirmedException(String message) {
        super(message);
    }
}
