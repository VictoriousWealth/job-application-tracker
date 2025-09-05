package com.nick.job_application_tracker.exception.base_case;

// ┌───────────────┐
// │ Base Exception│
// └───────────────┘
public abstract class ApiException extends RuntimeException {
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
