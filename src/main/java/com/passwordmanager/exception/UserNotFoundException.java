package com.passwordmanager.exception;

// Custom exception to indicate that a user was not found in the system
// This will be handled and logged by GlobalExceptionHandler
public class UserNotFoundException extends RuntimeException {

    // Constructor: provide an error message when throwing this exception
    public UserNotFoundException(String message) {
        super(message);
    }
}