package com.sangdari.global.exception.custom;

public class PaymentAmountMismatchException extends RuntimeException {
    public PaymentAmountMismatchException(String message) {
        super(message);
    }
}
