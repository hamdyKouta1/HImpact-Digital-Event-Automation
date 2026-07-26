package com.himpact.exception;

/**
 * Thrown when a business rule is violated.
 * Maps to HTTP 409 Conflict or 422 Unprocessable Entity.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
