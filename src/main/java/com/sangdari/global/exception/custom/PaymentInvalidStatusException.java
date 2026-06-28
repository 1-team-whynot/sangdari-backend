package com.sangdari.global.exception.custom;

public class PaymentInvalidStatusException extends RuntimeException {
    public PaymentInvalidStatusException(String message) {
        super(message);
    }
}
