package com.esand.products.exception;

public class CategoryUniqueViolationException extends RuntimeException {
    public CategoryUniqueViolationException(String message) {
        super(message);
    }
}