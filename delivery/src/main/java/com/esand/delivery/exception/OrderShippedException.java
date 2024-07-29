package com.esand.delivery.exception;

import lombok.Getter;

@Getter
public class OrderShippedException extends RuntimeException {
    public OrderShippedException(String message){
        super(message);
    }
}
