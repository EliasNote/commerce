package com.esand.orders.exception;

import lombok.Getter;

@Getter
public class UnavailableProductException extends RuntimeException {
    public UnavailableProductException(String message) {
        super(message);
    }
}
