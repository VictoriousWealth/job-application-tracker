package com.nick.job_application_tracker.dto;

import java.util.UUID;

public final class LegacyIdAdapter {
    private LegacyIdAdapter() {}

    public static UUID fromLong(Long value) {
        return value == null ? null : new UUID(0L, value);
    }

    public static Long toLong(UUID value) {
        if (value == null) {
            return null;
        }
        return value.getMostSignificantBits() == 0L ? value.getLeastSignificantBits() : null;
    }
}
