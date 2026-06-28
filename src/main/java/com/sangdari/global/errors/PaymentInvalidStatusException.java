package com.sangdari.global.errors;

public class PaymentInvalidStatusException extends RuntimeException {
    public PaymentInvalidStatusException(String message) {
        super(message);
    }
}
