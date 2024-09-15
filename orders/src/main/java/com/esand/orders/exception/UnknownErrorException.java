package com.esand.orders.exception;

import lombok.Getter;

@Getter
public class UnknownErrorException extends RuntimeException {
    public UnknownErrorException(String message) {
        super(message);
    }
}
