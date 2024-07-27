package com.esand.orders.exception;

import lombok.Getter;

@Getter
public class ConnectionException extends RuntimeException {
    public ConnectionException(String message){
        super(message);
    }
}
