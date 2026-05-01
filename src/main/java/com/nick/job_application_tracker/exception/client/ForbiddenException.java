package com.nick.job_application_tracker.exception.client;

public class ForbiddenException extends ClientException {
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
