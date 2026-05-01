package com.nick.job_application_tracker.exception.client;

public class NotFoundException extends ClientException {
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
