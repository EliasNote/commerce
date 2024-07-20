package com.esand.products.exception;

public class SkuUniqueViolationException extends RuntimeException {
    public SkuUniqueViolationException(String message) {
        super(message);
    }
}