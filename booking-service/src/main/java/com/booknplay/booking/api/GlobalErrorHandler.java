package com.booknplay.booking.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now(),
                "error", "validation_error",
                "details", ex.getBindingResult().getFieldErrors().stream()
                        .map(f -> Map.of("field", f.getField(), "msg", f.getDefaultMessage()))
                        .toList()
        ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", Instant.now(),
                "error", "conflict",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleGeneric(Throwable ex) {
        return ResponseEntity.status(500).body(Map.of(
                "timestamp", Instant.now(),
                "error", "internal",
                "message", ex.getMessage()
        ));
    }
}
