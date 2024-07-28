package com.esand.products.exception;

public class InvalidProductStatusException extends RuntimeException {
    public InvalidProductStatusException(String message) {
        super(message);
    }
}