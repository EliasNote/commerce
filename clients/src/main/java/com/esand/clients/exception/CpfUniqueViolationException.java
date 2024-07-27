package com.esand.clients.exception;

import lombok.Getter;

@Getter
public class CpfUniqueViolationException extends RuntimeException {
    public CpfUniqueViolationException(String message) {
        super(message);
    }
}