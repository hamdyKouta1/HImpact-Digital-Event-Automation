package com.himpact.exception;

/**
 * Thrown when authentication fails — invalid token, suspended account, etc.
 * Maps to HTTP 401 Unauthorized.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
