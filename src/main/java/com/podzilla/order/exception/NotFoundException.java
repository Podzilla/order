package com.podzilla.order.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(final String message) {
        super("Not Found: " + message);
    }
}
