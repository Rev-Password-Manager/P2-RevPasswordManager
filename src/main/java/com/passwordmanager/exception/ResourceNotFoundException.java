package com.passwordmanager.exception;

// Custom exception to indicate that a requested resource was not found
// It will be caught by GlobalExceptionHandler to send a proper HTTP response
public class ResourceNotFoundException extends RuntimeException {

    // Constructor: pass the error message when throwing this exception
    public ResourceNotFoundException(String message) {
        super(message);
    }
}