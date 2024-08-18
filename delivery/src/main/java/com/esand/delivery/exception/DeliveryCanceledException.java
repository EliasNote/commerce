package com.esand.delivery.exception;

import lombok.Getter;

@Getter
public class DeliveryCanceledException extends RuntimeException {
    public DeliveryCanceledException(String message){
        super(message);
    }
}
