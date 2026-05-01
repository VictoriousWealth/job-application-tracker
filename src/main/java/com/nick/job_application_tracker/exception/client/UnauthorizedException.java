package com.nick.job_application_tracker.exception.client;

public class UnauthorizedException extends ClientException {
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
