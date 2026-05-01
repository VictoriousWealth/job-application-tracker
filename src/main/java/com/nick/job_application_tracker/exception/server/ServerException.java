package com.nick.job_application_tracker.exception.server;

import com.nick.job_application_tracker.exception.base.ApiException;

public abstract class ServerException extends ApiException {
    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
