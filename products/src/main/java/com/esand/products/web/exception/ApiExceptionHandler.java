package com.esand.products.web.exception;

import com.esand.products.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<ErrorMessage> EntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        log.error("API Error", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(SkuUniqueViolationException.class)
    public final ResponseEntity<ErrorMessage> SkuUniqueViolationException(SkuUniqueViolationException ex, HttpServletRequest request) {
        log.error("API Error", ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(TitleUniqueViolationException.class)
    public final ResponseEntity<ErrorMessage> TitleUniqueViolationException(TitleUniqueViolationException ex, HttpServletRequest request) {
        log.error("API Error", ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public final ResponseEntity<ErrorMessage> InvalidQuantityException(InvalidQuantityException ex, HttpServletRequest request) {
        log.error("API Error", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(InvalidCategoryException.class)
    public final ResponseEntity<ErrorMessage> InvalidCategoryException(InvalidCategoryException ex, HttpServletRequest request) {
        log.error("API Error", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("API Error", ex);
        ErrorMessage errorMessage = new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Invalid request content.", ex.getBindingResult());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMessage);
    }
}
