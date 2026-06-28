package com.sangdari.global.errors;

public class PaymentDuplicatedException extends RuntimeException {
    public PaymentDuplicatedException(String message) {
        super(message);
    }
}
