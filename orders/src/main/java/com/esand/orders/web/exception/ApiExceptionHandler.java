package com.esand.orders.web.exception;

import com.esand.orders.exception.ConnectionException;
import com.esand.orders.exception.EntityNotFoundException;
import com.esand.orders.exception.InvalidQuantityException;
import com.esand.orders.exception.UnavailableProductException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<ErrorMessage> entityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        log.error("API Error", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public final ResponseEntity<ErrorMessage> invalidQuantityException(InvalidQuantityException ex, HttpServletRequest request) {
        log.error("API Error", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(UnavailableProductException.class)
    public final ResponseEntity<ErrorMessage> unavailableProductException(UnavailableProductException ex, HttpServletRequest request) {
        log.error("API Error", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ConnectionException.class)
    public final ResponseEntity<ErrorMessage> connectionException(ConnectionException ex, HttpServletRequest request) {
        log.error("Erro na API", ex);
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage()));
    }

}
