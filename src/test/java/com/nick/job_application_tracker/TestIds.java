package com.nick.job_application_tracker;

import java.util.UUID;

public final class TestIds {
    private TestIds() {}

    public static UUID uuid(long value) {
        return new UUID(0L, value);
    }
}
