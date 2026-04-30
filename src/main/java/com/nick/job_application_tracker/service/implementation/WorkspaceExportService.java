package com.nick.job_application_tracker.service.implementation;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nick.job_application_tracker.dto.response.AnalyticsResponseDTO;
import com.nick.job_application_tracker.dto.response.CalendarEventResponseDTO;
import com.nick.job_application_tracker.dto.response.RecommendationResponseDTO;
import com.nick.job_application_tracker.dto.response.WorkspaceApplicationExportResponseDTO;
import com.nick.job_application_tracker.dto.response.WorkspaceExportResponseDTO;
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.model.SkillTracker;
import com.nick.job_application_tracker.model.User;

@Service
public class WorkspaceExportService {
    private static final MediaType TEXT_CSV = MediaType.parseMediaType("text/csv");
    private static final MediaType APPLICATION_PDF = MediaType.parseMediaType("application/pdf");
    private static final DateTimeFormatter EXPORT_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final WorkspaceReadService workspaceReadService;
    private final WorkspaceInsightsService workspaceInsightsService;
    private final CalendarIntegrationService calendarIntegrationService;
    private final ObjectMapper objectMapper;

    public WorkspaceExportService(
        WorkspaceReadService workspaceReadService,
        WorkspaceInsightsService workspaceInsightsService,
        CalendarIntegrationService calendarIntegrationService,
        ObjectMapper objectMapper
    ) {
        this.workspaceReadService = workspaceReadService;
        this.workspaceInsightsService = workspaceInsightsService;
        this.calendarIntegrationService = calendarIntegrationService;
        this.objectMapper = objectMapper;
    }

    public ExportPayload exportWorkspace(String format) {
        ExportFormat exportFormat = ExportFormat.from(format);
        WorkspaceExportResponseDTO response = buildWorkspaceExport();
        String timestamp = LocalDateTime.now().format(EXPORT_TIMESTAMP);

        try {
            return switch (exportFormat) {
                case JSON -> new ExportPayload(
                    objectMapper.writeValueAsBytes(response),
                    "job-application-workspace-" + timestamp + ".json",
                    MediaType.APPLICATION_JSON
                );
                case CSV -> new ExportPayload(
                    buildCsv(response).getBytes(StandardCharsets.UTF_8),
                    "job-application-workspace-" + timestamp + ".csv",
                    TEXT_CSV
                );
                case PDF -> new ExportPayload(
                    buildPdf(response),
                    "job-application-workspace-" + timestamp + ".pdf",
                    APPLICATION_PDF
                );
            };
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to export workspace data.", ex);
        }
    }

    public WorkspaceExportResponseDTO buildWorkspaceExport() {
        User currentUser = workspaceReadService.getCurrentUser();
        List<JobApplication> applications = workspaceReadService.getJobApplications();
        List<Attachment> attachments = workspaceReadService.getAttachments();
        List<CommunicationLog> communications = workspaceReadService.getCommunicationLogs();
        List<ApplicationTimeline> timelineEntries = workspaceReadService.getTimelineEntries();
        List<SkillTracker> skills = workspaceReadService.getSkills();
        List<FollowUpReminder> reminders = workspaceReadService.getFollowUpReminders();
        List<ScheduledCommunication> scheduledCommunications = workspaceReadService.getScheduledCommunications();
        List<CalendarEventResponseDTO> calendarEvents = calendarIntegrationService.getEvents(null, null);
        AnalyticsResponseDTO analytics = workspaceInsightsService.getAnalytics(null, null);
        RecommendationResponseDTO recommendations = workspaceInsightsService.getRecommendations();

        Map<UUID, List<Attachment>> attachmentsByApplication = attachments.stream()
            .collect(Collectors.groupingBy(item -> item.getJobApplication().getId()));
        Map<UUID, List<CommunicationLog>> communicationsByApplication = communications.stream()
            .collect(Collectors.groupingBy(item -> item.getJobApplication().getId()));
        Map<UUID, List<ApplicationTimeline>> timelineByApplication = timelineEntries.stream()
            .collect(Collectors.groupingBy(item -> item.getJobApplication().getId()));
        Map<UUID, List<SkillTracker>> skillsByApplication = skills.stream()
            .collect(Collectors.groupingBy(item -> item.getJobApplication().getId()));
        Map<UUID, List<FollowUpReminder>> remindersByApplication = reminders.stream()
            .collect(Collectors.groupingBy(item -> item.getJobApplication().getId()));
        Map<UUID, List<ScheduledCommunication>> schedulesByApplication = scheduledCommunications.stream()
            .collect(Collectors.groupingBy(item -> item.getJobApplication().getId()));

        List<WorkspaceApplicationExportResponseDTO> exportedApplications = applications.stream()
            .sorted(Comparator.comparing(JobApplication::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
            .map(application -> {
                UUID applicationId = application.getId();
                List<CalendarEventResponseDTO> applicationEvents = calendarEvents.stream()
                    .filter(event -> applicationId.equals(event.applicationId()))
                    .toList();

                List<CalendarEventResponseDTO> scheduledEvents = applicationEvents.stream()
                    .filter(event -> "SCHEDULED_COMMUNICATION".equals(event.type()))
                    .toList();

                List<CalendarEventResponseDTO> reminderEvents = applicationEvents.stream()
                    .filter(event -> "FOLLOW_UP_REMINDER".equals(event.type()))
                    .toList();

                return new WorkspaceApplicationExportResponseDTO(
                    applicationId,
                    application.getCompany(),
                    application.getJobTitle(),
                    application.getStatus() == null ? null : application.getStatus().name(),
                    application.getSource() == null ? null : application.getSource().getName(),
                    application.getLocation() == null ? null : application.getLocation().getCity() + ", " + application.getLocation().getCountry(),
                    application.getCreatedAt(),
                    application.getAppliedOn(),
                    application.getDeadline(),
                    application.getResume() == null ? null : application.getResume().getTitle(),
                    application.getCoverLetter() == null ? null : application.getCoverLetter().getTitle(),
                    application.getNotes(),
                    application.getJobDescription(),
                    skillsByApplication.getOrDefault(applicationId, List.of()).stream()
                        .map(SkillTracker::getSkillName)
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .toList(),
                    attachmentsByApplication.getOrDefault(applicationId, List.of()).stream()
                        .map(Attachment::getFilePath)
                        .toList(),
                    communicationsByApplication.getOrDefault(applicationId, List.of()).stream()
                        .sorted(Comparator.comparing(CommunicationLog::getTimestamp, Comparator.nullsLast(LocalDateTime::compareTo)))
                        .map(item -> item.getTimestamp() + " " + item.getDirection() + " " + item.getType() + ": " + abbreviate(item.getMessage(), 120))
                        .toList(),
                    timelineByApplication.getOrDefault(applicationId, List.of()).stream()
                        .sorted(Comparator.comparing(ApplicationTimeline::getEventTime, Comparator.nullsLast(LocalDateTime::compareTo)))
                        .map(item -> item.getEventTime() + " " + item.getEventType() + ": " + abbreviate(item.getDescription(), 120))
                        .toList(),
                    scheduledEvents,
                    reminderEvents
                );
            })
            .toList();

        return new WorkspaceExportResponseDTO(
            LocalDateTime.now(),
            currentUser.getEmail(),
            analytics,
            recommendations,
            exportedApplications,
            calendarEvents
        );
    }

    private String buildCsv(WorkspaceExportResponseDTO response) {
        StringBuilder builder = new StringBuilder();
        builder.append("applicationId,company,jobTitle,status,source,location,createdAt,appliedOn,deadline,resumeTitle,coverLetterTitle,skillCount,attachmentCount,communicationCount,reminderCount,scheduledEventCount,timelineEventCount\n");

        for (WorkspaceApplicationExportResponseDTO application : response.applications()) {
            builder.append(csv(application.applicationId()))
                .append(',').append(csv(application.company()))
                .append(',').append(csv(application.jobTitle()))
                .append(',').append(csv(application.status()))
                .append(',').append(csv(application.sourceName()))
                .append(',').append(csv(application.location()))
                .append(',').append(csv(application.createdAt()))
                .append(',').append(csv(application.appliedOn()))
                .append(',').append(csv(application.deadline()))
                .append(',').append(csv(application.resumeTitle()))
                .append(',').append(csv(application.coverLetterTitle()))
                .append(',').append(application.skills().size())
                .append(',').append(application.attachments().size())
                .append(',').append(application.communications().size())
                .append(',').append(application.followUpReminders().size())
                .append(',').append(application.scheduledCommunications().size())
                .append(',').append(application.timeline().size())
                .append('\n');
        }

        return builder.toString();
    }

    private byte[] buildPdf(WorkspaceExportResponseDTO response) {
        List<String> lines = new ArrayList<>();
        lines.add("Job Application Tracker Workspace Export");
        lines.add("Generated: " + response.exportedAt());
        lines.add("User: " + response.userEmail());
        lines.add("");
        lines.add("Applications: " + response.analytics().totalApplications());
        lines.add("Applied: " + response.analytics().appliedCount()
            + " | Interview Stage: " + response.analytics().interviewStageCount()
            + " | Offers: " + response.analytics().offerCount()
            + " | Rejected: " + response.analytics().rejectedCount());
        lines.add("Overdue Reminders: " + response.analytics().overdueReminders()
            + " | Upcoming Events: " + response.analytics().upcomingScheduledEvents());
        lines.add("");

        for (WorkspaceApplicationExportResponseDTO application : response.applications()) {
            lines.add(application.company() + " | " + application.jobTitle() + " | " + application.status());
            lines.add("Source: " + safe(application.sourceName()) + " | Location: " + safe(application.location()));
            lines.add("Applied: " + safe(application.appliedOn()) + " | Deadline: " + safe(application.deadline()));
            lines.add("Resume: " + safe(application.resumeTitle()) + " | Cover Letter: " + safe(application.coverLetterTitle()));
            lines.add("Skills: " + String.join(", ", application.skills()));
            lines.add("Attachments: " + application.attachments().size()
                + " | Communications: " + application.communications().size()
                + " | Reminders: " + application.followUpReminders().size()
                + " | Scheduled: " + application.scheduledCommunications().size());
            if (application.timeline().isEmpty()) {
                lines.add("Latest Timeline: None");
            } else {
                lines.add("Latest Timeline: " + abbreviate(application.timeline().get(application.timeline().size() - 1), 110));
            }
            lines.add("");
        }

        return createSimplePdf(lines);
    }

    private byte[] createSimplePdf(List<String> lines) {
        List<List<String>> pages = paginate(lines, 40);
        int pageCount = pages.size();
        int firstPageObject = 3;
        int firstContentObject = firstPageObject + pageCount;
        int fontObject = firstContentObject + pageCount;
        int totalObjects = fontObject;

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>(java.util.Collections.nCopies(totalObjects + 1, 0));
        write(output, "%PDF-1.4\n");

        offsets.set(1, output.size());
        write(output, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        offsets.set(2, output.size());
        String kids = java.util.stream.IntStream.range(0, pageCount)
            .mapToObj(index -> (firstPageObject + index) + " 0 R")
            .collect(Collectors.joining(" "));
        write(output, "2 0 obj\n<< /Type /Pages /Kids [ " + kids + " ] /Count " + pageCount + " >>\nendobj\n");

        for (int i = 0; i < pageCount; i++) {
            int pageObject = firstPageObject + i;
            int contentObject = firstContentObject + i;

            offsets.set(pageObject, output.size());
            write(output, pageObject + " 0 obj\n");
            write(output, "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents " + contentObject + " 0 R /Resources << /Font << /F1 " + fontObject + " 0 R >> >> >>\n");
            write(output, "endobj\n");

            String stream = buildPdfStream(pages.get(i));
            byte[] streamBytes = stream.getBytes(StandardCharsets.ISO_8859_1);
            offsets.set(contentObject, output.size());
            write(output, contentObject + " 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n");
            output.writeBytes(streamBytes);
            write(output, "\nendstream\nendobj\n");
        }

        offsets.set(fontObject, output.size());
        write(output, fontObject + " 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n");

        int xrefStart = output.size();
        write(output, "xref\n0 " + (totalObjects + 1) + "\n");
        write(output, "0000000000 65535 f \n");
        for (int i = 1; i <= totalObjects; i++) {
            write(output, String.format("%010d 00000 n \n", offsets.get(i)));
        }
        write(output, "trailer\n<< /Size " + (totalObjects + 1) + " /Root 1 0 R >>\n");
        write(output, "startxref\n" + xrefStart + "\n%%EOF");
        return output.toByteArray();
    }

    private List<List<String>> paginate(List<String> lines, int pageSize) {
        List<List<String>> pages = new ArrayList<>();
        for (int start = 0; start < lines.size(); start += pageSize) {
            pages.add(lines.subList(start, Math.min(lines.size(), start + pageSize)));
        }
        return pages.isEmpty() ? List.of(List.of("")) : pages;
    }

    private String buildPdfStream(List<String> lines) {
        StringBuilder builder = new StringBuilder();
        builder.append("BT\n");
        builder.append("/F1 11 Tf\n");
        builder.append("50 760 Td\n");
        builder.append("14 TL\n");
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                builder.append("T*\n");
            }
            builder.append("(").append(escapePdf(lines.get(i))).append(") Tj\n");
        }
        builder.append("ET");
        return builder.toString();
    }

    private String escapePdf(String value) {
        String sanitized = value == null ? "" : value.replaceAll("[^\\x20-\\x7E]", "?");
        return sanitized
            .replace("\\", "\\\\")
            .replace("(", "\\(")
            .replace(")", "\\)");
    }

    private void write(ByteArrayOutputStream output, String value) {
        output.writeBytes(value.getBytes(StandardCharsets.ISO_8859_1));
    }

    private String csv(Object value) {
        String text = value == null ? "" : value.toString();
        String escaped = text.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private String abbreviate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }

    private String safe(Object value) {
        return value == null ? "-" : value.toString();
    }

    public record ExportPayload(byte[] content, String filename, MediaType contentType) {
    }

    enum ExportFormat {
        JSON,
        CSV,
        PDF;

        static ExportFormat from(String value) {
            if (value == null || value.isBlank()) {
                return JSON;
            }
            try {
                return ExportFormat.valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Unsupported export format: " + value + ". Expected one of: json, csv, pdf.");
            }
        }
    }
}
