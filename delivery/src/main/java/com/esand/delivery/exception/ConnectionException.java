package com.esand.delivery.exception;

import lombok.Getter;

@Getter
public class ConnectionException extends RuntimeException {
    public ConnectionException(String message){
        super(message);
    }
}
