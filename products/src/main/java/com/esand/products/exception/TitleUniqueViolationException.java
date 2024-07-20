package com.esand.products.exception;

public class TitleUniqueViolationException extends RuntimeException {
    public TitleUniqueViolationException(String message) {
        super(message);
    }
}