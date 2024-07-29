package com.esand.delivery.exception;

import lombok.Getter;

@Getter
public class OrderCanceledException extends RuntimeException {
    public OrderCanceledException(String message){
        super(message);
    }
}
