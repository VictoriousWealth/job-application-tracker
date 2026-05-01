package com.nick.job_application_tracker.exception.server;

public class ServiceUnavailableException extends ServerException {
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
