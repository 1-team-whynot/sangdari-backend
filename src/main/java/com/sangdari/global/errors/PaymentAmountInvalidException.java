package com.sangdari.global.errors;

public class PaymentAmountInvalidException extends RuntimeException {
    public PaymentAmountInvalidException(String message) {
        super(message);
    }
}
