package com.nick.job_application_tracker.service.implementation;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.response.CalendarEventResponseDTO;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.ScheduledCommunication;

@Service
public class CalendarIntegrationService {
    private static final MediaType TEXT_CALENDAR = MediaType.parseMediaType("text/calendar");
    private static final DateTimeFormatter ICS_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    private static final DateTimeFormatter EXPORT_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final WorkspaceReadService workspaceReadService;

    public CalendarIntegrationService(WorkspaceReadService workspaceReadService) {
        this.workspaceReadService = workspaceReadService;
    }

    public List<CalendarEventResponseDTO> getEvents(LocalDate from, LocalDate to) {
        LocalDateTime rangeStart = from == null ? LocalDate.now().atStartOfDay() : from.atStartOfDay();
        LocalDateTime rangeEnd = to == null ? LocalDate.now().plusDays(90).plusDays(1).atStartOfDay() : to.plusDays(1).atStartOfDay();

        List<CalendarEventResponseDTO> events = new ArrayList<>();
        for (ScheduledCommunication communication : workspaceReadService.getScheduledCommunications()) {
            if (communication.getScheduledFor() == null || !isWithinWindow(communication.getScheduledFor(), rangeStart, rangeEnd)) {
                continue;
            }
            events.add(toScheduledEvent(communication));
        }

        for (FollowUpReminder reminder : workspaceReadService.getFollowUpReminders()) {
            if (reminder.getRemindOn() == null || !isWithinWindow(reminder.getRemindOn(), rangeStart, rangeEnd)) {
                continue;
            }
            events.add(toReminderEvent(reminder));
        }

        events.sort(Comparator.comparing(CalendarEventResponseDTO::startsAt));
        return events;
    }

    public CalendarExportPayload exportCalendar(LocalDate from, LocalDate to) {
        List<CalendarEventResponseDTO> events = getEvents(from, to);
        StringBuilder builder = new StringBuilder();
        String timestamp = LocalDateTime.now(ZoneOffset.UTC).format(ICS_TIMESTAMP);

        builder.append("BEGIN:VCALENDAR\r\n");
        builder.append("VERSION:2.0\r\n");
        builder.append("PRODID:-//Job Application Tracker//Calendar Export//EN\r\n");
        builder.append("CALSCALE:GREGORIAN\r\n");
        builder.append("METHOD:PUBLISH\r\n");

        for (CalendarEventResponseDTO event : events) {
            builder.append("BEGIN:VEVENT\r\n");
            builder.append("UID:").append(buildUid(event)).append("\r\n");
            builder.append("DTSTAMP:").append(timestamp).append("\r\n");
            builder.append("DTSTART:").append(toUtcTimestamp(event.startsAt())).append("\r\n");
            builder.append("DTEND:").append(toUtcTimestamp(event.endsAt())).append("\r\n");
            builder.append("SUMMARY:").append(escapeIcs(event.title())).append("\r\n");
            builder.append("DESCRIPTION:").append(escapeIcs(event.description())).append("\r\n");
            builder.append("CATEGORIES:").append(escapeIcs(event.type())).append("\r\n");
            builder.append("END:VEVENT\r\n");
        }

        builder.append("END:VCALENDAR\r\n");

        String filename = "job-application-calendar-" + LocalDateTime.now().format(EXPORT_TIMESTAMP) + ".ics";
        return new CalendarExportPayload(builder.toString().getBytes(StandardCharsets.UTF_8), filename, TEXT_CALENDAR);
    }

    private CalendarEventResponseDTO toScheduledEvent(ScheduledCommunication communication) {
        JobApplication application = communication.getJobApplication();
        LocalDateTime startsAt = communication.getScheduledFor();
        LocalDateTime endsAt = startsAt.plus(resolveDuration(communication));
        String title = switch (communication.getType()) {
            case INTERVIEW -> "Interview: " + application.getCompany() + " - " + application.getJobTitle();
            case ONLINE_ASSESSMENT, IN_PERSON_ASSESSMENT -> "Assessment: " + application.getCompany() + " - " + application.getJobTitle();
            case CALL -> "Call: " + application.getCompany() + " - " + application.getJobTitle();
        };

        return new CalendarEventResponseDTO(
            "SCHEDULED_COMMUNICATION",
            application.getId(),
            application.getCompany(),
            application.getJobTitle(),
            startsAt,
            endsAt,
            title,
            communication.getNotes()
        );
    }

    private CalendarEventResponseDTO toReminderEvent(FollowUpReminder reminder) {
        JobApplication application = reminder.getJobApplication();
        LocalDateTime startsAt = reminder.getRemindOn();
        LocalDateTime endsAt = startsAt.plusMinutes(15);
        return new CalendarEventResponseDTO(
            "FOLLOW_UP_REMINDER",
            application.getId(),
            application.getCompany(),
            application.getJobTitle(),
            startsAt,
            endsAt,
            "Follow up: " + application.getCompany() + " - " + application.getJobTitle(),
            reminder.getNote()
        );
    }

    private Duration resolveDuration(ScheduledCommunication communication) {
        return switch (communication.getType()) {
            case INTERVIEW -> Duration.ofHours(1);
            case ONLINE_ASSESSMENT, IN_PERSON_ASSESSMENT -> Duration.ofMinutes(90);
            case CALL -> Duration.ofMinutes(30);
        };
    }

    private boolean isWithinWindow(LocalDateTime value, LocalDateTime start, LocalDateTime endExclusive) {
        return !value.isBefore(start) && value.isBefore(endExclusive);
    }

    private String buildUid(CalendarEventResponseDTO event) {
        return UUID.nameUUIDFromBytes((event.type() + "|" + event.applicationId() + "|" + event.startsAt()).getBytes(StandardCharsets.UTF_8))
            + "@job-application-tracker";
    }

    private String toUtcTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).format(ICS_TIMESTAMP);
    }

    private String escapeIcs(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("\\", "\\\\")
            .replace(";", "\\;")
            .replace(",", "\\,")
            .replace("\r\n", "\\n")
            .replace("\n", "\\n");
    }

    public record CalendarExportPayload(byte[] content, String filename, MediaType contentType) {
    }
}
