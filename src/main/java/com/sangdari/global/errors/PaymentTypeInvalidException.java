package com.sangdari.global.errors;

public class PaymentTypeInvalidException extends RuntimeException {
    public PaymentTypeInvalidException(String message) {
        super(message);
    }
}
