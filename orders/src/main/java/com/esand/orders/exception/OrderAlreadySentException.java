package com.esand.orders.exception;

import lombok.Getter;

@Getter
public class OrderAlreadySentException extends RuntimeException {
    public OrderAlreadySentException(String message) {
        super(message);
    }
}