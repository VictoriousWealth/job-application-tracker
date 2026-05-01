package com.nick.job_application_tracker.exception.server;

public class InternalServerErrorException extends ServerException {
    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
