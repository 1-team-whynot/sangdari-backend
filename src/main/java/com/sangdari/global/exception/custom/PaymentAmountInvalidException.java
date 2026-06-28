package com.sangdari.global.exception.custom;

public class PaymentAmountInvalidException extends RuntimeException {
    public PaymentAmountInvalidException(String message) {
        super(message);
    }
}
