package com.nick.job_application_tracker.exception.client.specialized;

/**
 * Thrown when a client provides a field that doesn't exist or is not allowed.
 */
public class InvalidFieldException extends PatchException {
    public InvalidFieldException(String fieldName, Throwable cause) {
        super("Invalid or unknown field: " + fieldName, cause);
    }
}
