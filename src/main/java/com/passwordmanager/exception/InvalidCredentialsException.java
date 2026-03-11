package com.passwordmanager.exception;

// Custom exception to indicate that login credentials are invalid
// This will be caught by GlobalExceptionHandler for proper HTTP response
public class InvalidCredentialsException extends RuntimeException {

    // Constructor: pass the error message when throwing this exception
    public InvalidCredentialsException(String message) {
        super(message);
    }
}