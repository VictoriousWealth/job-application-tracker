package com.nick.job_application_tracker.exception.client_exception;

public class BadRequestException extends ClientException {
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
