package com.sangdari.global.exception.custom;

public class PaymentDuplicatedException extends RuntimeException {
    public PaymentDuplicatedException(String message) {
        super(message);
    }
}
