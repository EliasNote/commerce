package com.esand.delivery.web.exception;

import com.esand.delivery.exception.ConnectionException;
import com.esand.delivery.exception.EntityNotFoundException;
import com.esand.delivery.exception.OrderCanceledException;
import com.esand.delivery.exception.OrderShippedException;
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

    @ExceptionHandler(OrderShippedException.class)
    public final ResponseEntity<ErrorMessage> orderCancelledException(OrderShippedException ex, HttpServletRequest request) {
        log.error("API Error", ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(OrderCanceledException.class)
    public final ResponseEntity<ErrorMessage> orderCancelledException(OrderCanceledException ex, HttpServletRequest request) {
        log.error("API Error", ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<ErrorMessage> entityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
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
