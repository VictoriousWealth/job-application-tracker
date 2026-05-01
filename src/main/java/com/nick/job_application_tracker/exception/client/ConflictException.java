package com.nick.job_application_tracker.exception.client;

public class ConflictException extends ClientException {
    public ConflictException(String message) {
        super(message, null);
    }
}
