package com.nick.job_application_tracker.exception.client;

import com.nick.job_application_tracker.exception.base.ApiException;

public abstract class ClientException extends ApiException {
    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
