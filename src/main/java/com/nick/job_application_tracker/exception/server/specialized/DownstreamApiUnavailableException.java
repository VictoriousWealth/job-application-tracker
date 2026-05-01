package com.nick.job_application_tracker.exception.server.specialized;

import com.nick.job_application_tracker.exception.server.ServiceUnavailableException;

public class DownstreamApiUnavailableException extends ServiceUnavailableException {
    public DownstreamApiUnavailableException(String serviceName, Throwable cause) {
        super(serviceName + " is currently unavailable.", cause);
    }
}
