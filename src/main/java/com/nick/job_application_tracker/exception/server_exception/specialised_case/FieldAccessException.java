package com.nick.job_application_tracker.exception.server_exception.specialised_case;

import com.nick.job_application_tracker.exception.server_exception.InternalServerErrorException;

/**
 * Thrown when a field cannot be accessed/reflected during dynamic operations.
 */
public class FieldAccessException extends InternalServerErrorException {
    public FieldAccessException(String fieldName, Throwable cause) {
        super("Unable to access or modify field: " + fieldName, cause);
    }
}
