package com.esand.orders.exception;

import lombok.Getter;

@Getter
public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException(String message) {
        super(message);
    }
}