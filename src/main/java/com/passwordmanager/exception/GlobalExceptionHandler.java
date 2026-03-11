package com.passwordmanager.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // for logging
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // =========================
    // USER NOT FOUND EXCEPTION
    // =========================
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
        logger.error("UserNotFoundException: {}", ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // =========================
    // INVALID CREDENTIALS EXCEPTION
    // =========================
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {
        logger.error("InvalidCredentialsException: {}", ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // =========================
    // RESOURCE NOT FOUND EXCEPTION
    // =========================
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.error("ResourceNotFoundException: {}", ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // =========================
    // GENERIC EXCEPTION HANDLER
    // =========================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        logger.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        return buildResponse("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // =========================
    // HELPER METHOD TO BUILD RESPONSE
    // =========================
    private ResponseEntity<?> buildResponse(String message, HttpStatus status) {
        // Log the response being sent
        logger.info("Returning error response: status={}, message={}", status.value(), message);

        return new ResponseEntity<>(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", status.value(),
                        "error", message
                ),
                status
        );
    }
}