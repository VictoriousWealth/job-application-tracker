package com.nick.job_application_tracker.exception.client_exception;

public class RateLimitExceededException extends ClientException {
    public RateLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
