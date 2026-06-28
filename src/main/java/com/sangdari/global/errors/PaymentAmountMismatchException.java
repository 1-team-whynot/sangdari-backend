package com.sangdari.global.errors;

public class PaymentAmountMismatchException extends RuntimeException {
    public PaymentAmountMismatchException(String message) {
        super(message);
    }
}
