package com.nick.job_application_tracker.exception.server_exception;

import com.nick.job_application_tracker.exception.base_case.ApiException;

public abstract class ServerException extends ApiException {
    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
