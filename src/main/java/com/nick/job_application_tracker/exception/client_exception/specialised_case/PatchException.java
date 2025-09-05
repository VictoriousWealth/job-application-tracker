package com.nick.job_application_tracker.exception.client_exception.specialised_case;

import com.nick.job_application_tracker.exception.client_exception.BadRequestException;

/**
 * Base exception for all patch/partial update related errors.
 */
public abstract class PatchException extends BadRequestException {
    public PatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
