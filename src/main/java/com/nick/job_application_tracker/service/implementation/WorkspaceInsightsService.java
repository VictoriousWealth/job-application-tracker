package com.nick.job_application_tracker.service.implementation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.response.AnalyticsResponseDTO;
import com.nick.job_application_tracker.dto.response.CalendarEventResponseDTO;
import com.nick.job_application_tracker.dto.response.DashboardResponseDTO;
import com.nick.job_application_tracker.dto.response.RecommendationItemResponseDTO;
import com.nick.job_application_tracker.dto.response.RecommendationResponseDTO;
import com.nick.job_application_tracker.dto.response.SourceAnalyticsResponseDTO;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.JobApplication.Status;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.model.ScheduledCommunication;

@Service
public class WorkspaceInsightsService {

    private final WorkspaceReadService workspaceReadService;
    private final CalendarIntegrationService calendarIntegrationService;

    public WorkspaceInsightsService(
        WorkspaceReadService workspaceReadService,
        CalendarIntegrationService calendarIntegrationService
    ) {
        this.workspaceReadService = workspaceReadService;
        this.calendarIntegrationService = calendarIntegrationService;
    }

    public DashboardResponseDTO getDashboard(LocalDate since, LocalDate until) {
        List<JobApplication> allApplications = workspaceReadService.getJobApplications();
        List<JobApplication> filteredApplications = filterApplications(allApplications, since, until);

        Map<String, Long> statusBreakdown = countBy(filteredApplications, job -> job.getStatus() == null ? "UNKNOWN" : job.getStatus().name());
        Map<String, Long> sourceBreakdown = countBy(filteredApplications, job -> job.getSource() == null ? "Unspecified" : job.getSource().getName());
        Map<String, Long> locationBreakdown = countBy(filteredApplications, this::formatLocation);
        Map<String, Long> appliedTrend = filteredApplications.stream()
            .map(this::resolveReferenceDate)
            .filter(java.util.Objects::nonNull)
            .collect(Collectors.groupingBy(
                value -> value.toLocalDate().toString(),
                LinkedHashMap::new,
                Collectors.counting()
            ));

        long activeApplications = filteredApplications.stream().filter(this::isActive).count();
        long closedApplications = filteredApplications.stream().filter(this::isClosed).count();
        List<CalendarEventResponseDTO> upcomingEvents = calendarIntegrationService.getEvents(LocalDate.now(), LocalDate.now().plusDays(30))
            .stream()
            .limit(5)
            .toList();

        return new DashboardResponseDTO(
            since,
            until,
            allApplications.size(),
            filteredApplications.size(),
            activeApplications,
            closedApplications,
            statusBreakdown,
            sourceBreakdown,
            locationBreakdown,
            appliedTrend,
            upcomingEvents
        );
    }

    public AnalyticsResponseDTO getAnalytics(LocalDate since, LocalDate until) {
        List<JobApplication> applications = filterApplications(workspaceReadService.getJobApplications(), since, until);
        List<FollowUpReminder> reminders = workspaceReadService.getFollowUpReminders();
        List<ScheduledCommunication> scheduledCommunications = workspaceReadService.getScheduledCommunications();

        long totalApplications = applications.size();
        long appliedCount = applications.stream().filter(job -> job.getStatus() != null && job.getStatus() != Status.DRAFT).count();
        long interviewStageCount = applications.stream().filter(this::hasReachedInterviewStage).count();
        long offerCount = applications.stream().filter(job -> job.getStatus() == Status.OFFER).count();
        long rejectedCount = applications.stream().filter(job -> job.getStatus() == Status.REJECTED).count();
        long activeApplications = applications.stream().filter(this::isActive).count();
        long overdueReminders = reminders.stream()
            .filter(reminder -> reminder.getRemindOn() != null && reminder.getRemindOn().isBefore(LocalDateTime.now()))
            .count();
        long upcomingScheduledEvents = scheduledCommunications.stream()
            .filter(item -> item.getScheduledFor() != null && !item.getScheduledFor().isBefore(LocalDateTime.now()))
            .count();

        List<SourceAnalyticsResponseDTO> sourcePerformance = applications.stream()
            .collect(Collectors.groupingBy(job -> job.getSource() == null ? "Unspecified" : job.getSource().getName()))
            .entrySet()
            .stream()
            .map(entry -> {
                List<JobApplication> bySource = entry.getValue();
                long sourceApplied = bySource.stream().filter(job -> job.getStatus() != null && job.getStatus() != Status.DRAFT).count();
                long sourceInterview = bySource.stream().filter(this::hasReachedInterviewStage).count();
                long sourceOffer = bySource.stream().filter(job -> job.getStatus() == Status.OFFER).count();
                return new SourceAnalyticsResponseDTO(
                    entry.getKey(),
                    bySource.size(),
                    sourceInterview,
                    sourceOffer,
                    toPercent(sourceInterview, sourceApplied),
                    toPercent(sourceOffer, sourceApplied)
                );
            })
            .sorted(Comparator.comparing(SourceAnalyticsResponseDTO::totalApplications).reversed()
                .thenComparing(SourceAnalyticsResponseDTO::sourceName))
            .toList();

        return new AnalyticsResponseDTO(
            totalApplications,
            appliedCount,
            interviewStageCount,
            offerCount,
            rejectedCount,
            toPercent(interviewStageCount, appliedCount),
            toPercent(offerCount, interviewStageCount),
            toPercent(offerCount, appliedCount),
            toPercent(activeApplications, totalApplications),
            overdueReminders,
            upcomingScheduledEvents,
            sourcePerformance
        );
    }

    public RecommendationResponseDTO getRecommendations() {
        List<JobApplication> applications = workspaceReadService.getJobApplications();
        List<FollowUpReminder> reminders = workspaceReadService.getFollowUpReminders();
        List<ScheduledCommunication> scheduledCommunications = workspaceReadService.getScheduledCommunications();
        List<Resume> resumes = workspaceReadService.getResumes();
        List<CoverLetter> coverLetters = workspaceReadService.getCoverLetters();

        LocalDateTime now = LocalDateTime.now();
        List<RecommendationItemResponseDTO> items = Stream.concat(
            buildReminderRecommendations(reminders, now).stream(),
            Stream.of(
                buildAgingApplicationRecommendations(applications, reminders, now),
                buildDocumentReuseRecommendations(applications, resumes, coverLetters),
                buildDraftRecommendations(applications, now),
                buildUpcomingEventRecommendations(scheduledCommunications, now)
            ).flatMap(List::stream)
        )
            .sorted(Comparator
                .comparingInt((RecommendationItemResponseDTO item) -> priorityRank(item.priority()))
                .thenComparing(RecommendationItemResponseDTO::company, Comparator.nullsLast(String::compareToIgnoreCase)))
            .limit(12)
            .toList();

        List<RecommendationItemResponseDTO> finalItems = items.isEmpty()
            ? List.of(new RecommendationItemResponseDTO(
                "MAINTAIN_MOMENTUM",
                "LOW",
                null,
                null,
                null,
                "Workspace is up to date",
                "No urgent follow-ups or missing-document issues were found.",
                "Review analytics and keep new applications flowing."
            ))
            : items;

        return new RecommendationResponseDTO(LocalDateTime.now(), finalItems);
    }

    private List<JobApplication> filterApplications(List<JobApplication> applications, LocalDate since, LocalDate until) {
        if (since == null && until == null) {
            return applications;
        }

        LocalDate effectiveSince = since;
        LocalDate effectiveUntil = until;
        if (effectiveSince != null && effectiveUntil != null && effectiveSince.isAfter(effectiveUntil)) {
            throw new IllegalArgumentException("'since' must be on or before 'until'.");
        }

        return applications.stream()
            .filter(job -> {
                LocalDateTime referenceDate = resolveReferenceDate(job);
                if (referenceDate == null) {
                    return false;
                }
                LocalDate day = referenceDate.toLocalDate();
                boolean afterStart = effectiveSince == null || !day.isBefore(effectiveSince);
                boolean beforeEnd = effectiveUntil == null || !day.isAfter(effectiveUntil);
                return afterStart && beforeEnd;
            })
            .toList();
    }

    private LocalDateTime resolveReferenceDate(JobApplication jobApplication) {
        return jobApplication.getAppliedOn() != null ? jobApplication.getAppliedOn() : jobApplication.getCreatedAt();
    }

    private Map<String, Long> countBy(List<JobApplication> applications, Function<JobApplication, String> classifier) {
        return applications.stream()
            .collect(Collectors.groupingBy(classifier, LinkedHashMap::new, Collectors.counting()));
    }

    private boolean isActive(JobApplication jobApplication) {
        return jobApplication.getStatus() == Status.DRAFT
            || jobApplication.getStatus() == Status.APPLIED
            || jobApplication.getStatus() == Status.INTERVIEW;
    }

    private boolean isClosed(JobApplication jobApplication) {
        return jobApplication.getStatus() == Status.OFFER || jobApplication.getStatus() == Status.REJECTED;
    }

    private boolean hasReachedInterviewStage(JobApplication jobApplication) {
        return jobApplication.getStatus() == Status.INTERVIEW || jobApplication.getStatus() == Status.OFFER;
    }

    private double toPercent(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0.0;
        }
        return Math.round((numerator * 1000.0 / denominator)) / 10.0;
    }

    private String formatLocation(JobApplication jobApplication) {
        if (jobApplication.getLocation() == null) {
            return "Unspecified";
        }
        return jobApplication.getLocation().getCity() + ", " + jobApplication.getLocation().getCountry();
    }

    private List<RecommendationItemResponseDTO> buildReminderRecommendations(List<FollowUpReminder> reminders, LocalDateTime now) {
        return reminders.stream()
            .filter(reminder -> reminder.getRemindOn() != null && reminder.getRemindOn().isBefore(now))
            .map(reminder -> {
                JobApplication application = reminder.getJobApplication();
                return new RecommendationItemResponseDTO(
                    "FOLLOW_UP_DUE",
                    "HIGH",
                    application.getId(),
                    application.getCompany(),
                    application.getJobTitle(),
                    "Follow-up is overdue",
                    "The reminder scheduled for " + reminder.getRemindOn() + " is already overdue.",
                    "Send the follow-up or reschedule the reminder immediately."
                );
            })
            .toList();
    }

    private List<RecommendationItemResponseDTO> buildAgingApplicationRecommendations(
        List<JobApplication> applications,
        List<FollowUpReminder> reminders,
        LocalDateTime now
    ) {
        Map<UUID, List<FollowUpReminder>> remindersByApplication = reminders.stream()
            .collect(Collectors.groupingBy(reminder -> reminder.getJobApplication().getId()));

        return applications.stream()
            .filter(job -> job.getStatus() == Status.APPLIED)
            .filter(job -> job.getAppliedOn() != null && job.getAppliedOn().isBefore(now.minusDays(7)))
            .filter(job -> remindersByApplication.getOrDefault(job.getId(), List.of()).stream()
                .noneMatch(reminder -> reminder.getRemindOn() != null && !reminder.getRemindOn().isBefore(now)))
            .map(job -> new RecommendationItemResponseDTO(
                "FOLLOW_UP_SUGGESTED",
                "MEDIUM",
                job.getId(),
                job.getCompany(),
                job.getJobTitle(),
                "Create a follow-up reminder",
                "This application has been in APPLIED status for more than 7 days without a future reminder.",
                "Create a reminder or send a check-in message."
            ))
            .toList();
    }

    private List<RecommendationItemResponseDTO> buildDocumentReuseRecommendations(
        List<JobApplication> applications,
        List<Resume> resumes,
        List<CoverLetter> coverLetters
    ) {
        return applications.stream()
            .filter(job -> job.getStatus() == Status.DRAFT || job.getStatus() == Status.APPLIED)
            .flatMap(job -> {
                List<RecommendationItemResponseDTO> items = new java.util.ArrayList<>();
                if (job.getResume() == null && !resumes.isEmpty()) {
                    items.add(new RecommendationItemResponseDTO(
                        "DOCUMENT_REUSE",
                        "MEDIUM",
                        job.getId(),
                        job.getCompany(),
                        job.getJobTitle(),
                        "Attach a stored resume",
                        "This application is missing a resume while reusable resumes already exist in the library.",
                        "Reuse one of your stored resumes before the next update."
                    ));
                }
                if (job.getCoverLetter() == null && !coverLetters.isEmpty()) {
                    items.add(new RecommendationItemResponseDTO(
                        "DOCUMENT_REUSE",
                        "MEDIUM",
                        job.getId(),
                        job.getCompany(),
                        job.getJobTitle(),
                        "Attach a stored cover letter",
                        "This application is missing a cover letter while reusable cover letters already exist in the library.",
                        "Reuse or tailor an existing cover letter for this role."
                    ));
                }
                return items.stream();
            })
            .toList();
    }

    private List<RecommendationItemResponseDTO> buildDraftRecommendations(List<JobApplication> applications, LocalDateTime now) {
        return applications.stream()
            .filter(job -> job.getStatus() == Status.DRAFT)
            .filter(job -> job.getCreatedAt() != null && job.getCreatedAt().isBefore(now.minusDays(3)))
            .map(job -> new RecommendationItemResponseDTO(
                "NEXT_BEST_ACTION",
                "LOW",
                job.getId(),
                job.getCompany(),
                job.getJobTitle(),
                "Finish this draft application",
                "The draft has been sitting for more than 3 days without being submitted.",
                "Review the job description, attach documents, and move it toward APPLIED."
            ))
            .toList();
    }

    private List<RecommendationItemResponseDTO> buildUpcomingEventRecommendations(
        List<ScheduledCommunication> scheduledCommunications,
        LocalDateTime now
    ) {
        return scheduledCommunications.stream()
            .filter(item -> item.getScheduledFor() != null)
            .filter(item -> !item.getScheduledFor().isBefore(now) && item.getScheduledFor().isBefore(now.plusDays(2)))
            .map(item -> {
                JobApplication application = item.getJobApplication();
                return new RecommendationItemResponseDTO(
                    "PREPARE_EVENT",
                    "HIGH",
                    application.getId(),
                    application.getCompany(),
                    application.getJobTitle(),
                    "Prepare for an upcoming event",
                    "A " + item.getType() + " is scheduled for " + item.getScheduledFor() + ".",
                    "Review the company, refresh your notes, and prepare questions."
                );
            })
            .toList();
    }

    private int priorityRank(String priority) {
        return switch (priority) {
            case "HIGH" -> 0;
            case "MEDIUM" -> 1;
            default -> 2;
        };
    }
}
