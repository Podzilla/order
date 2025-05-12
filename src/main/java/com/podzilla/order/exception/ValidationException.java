package com.podzilla.order.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(final String message) {
        super("Validation error: " + message);
    }
}
