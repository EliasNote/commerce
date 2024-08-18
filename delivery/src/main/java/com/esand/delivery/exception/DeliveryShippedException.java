package com.esand.delivery.exception;

import lombok.Getter;

@Getter
public class DeliveryShippedException extends RuntimeException {
    public DeliveryShippedException(String message){
        super(message);
    }
}
