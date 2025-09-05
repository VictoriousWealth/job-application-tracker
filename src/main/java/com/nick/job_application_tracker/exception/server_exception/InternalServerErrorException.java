package com.nick.job_application_tracker.exception.server_exception;

public class InternalServerErrorException extends ServerException {
    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
