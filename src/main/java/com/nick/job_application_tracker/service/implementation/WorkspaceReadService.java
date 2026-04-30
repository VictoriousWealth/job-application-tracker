package com.nick.job_application_tracker.service.implementation;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.model.SkillTracker;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.inter_face.ApplicationTimelineRepository;
import com.nick.job_application_tracker.repository.inter_face.AttachmentRepository;
import com.nick.job_application_tracker.repository.inter_face.CommunicationLogRepository;
import com.nick.job_application_tracker.repository.inter_face.CoverLetterRepository;
import com.nick.job_application_tracker.repository.inter_face.FollowUpReminderRepository;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.repository.inter_face.ResumeRepository;
import com.nick.job_application_tracker.repository.inter_face.ScheduledCommunicationRepository;
import com.nick.job_application_tracker.repository.inter_face.SkillTrackerRepository;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;
import com.nick.job_application_tracker.util.SecurityUtils;

@Service
public class WorkspaceReadService {

    private final JobApplicationRepository jobApplicationRepository;
    private final FollowUpReminderRepository followUpReminderRepository;
    private final ScheduledCommunicationRepository scheduledCommunicationRepository;
    private final CommunicationLogRepository communicationLogRepository;
    private final AttachmentRepository attachmentRepository;
    private final ApplicationTimelineRepository applicationTimelineRepository;
    private final SkillTrackerRepository skillTrackerRepository;
    private final ResumeRepository resumeRepository;
    private final CoverLetterRepository coverLetterRepository;
    private final UserRepository userRepository;

    public WorkspaceReadService(
        JobApplicationRepository jobApplicationRepository,
        FollowUpReminderRepository followUpReminderRepository,
        ScheduledCommunicationRepository scheduledCommunicationRepository,
        CommunicationLogRepository communicationLogRepository,
        AttachmentRepository attachmentRepository,
        ApplicationTimelineRepository applicationTimelineRepository,
        SkillTrackerRepository skillTrackerRepository,
        ResumeRepository resumeRepository,
        CoverLetterRepository coverLetterRepository,
        UserRepository userRepository
    ) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.followUpReminderRepository = followUpReminderRepository;
        this.scheduledCommunicationRepository = scheduledCommunicationRepository;
        this.communicationLogRepository = communicationLogRepository;
        this.attachmentRepository = attachmentRepository;
        this.applicationTimelineRepository = applicationTimelineRepository;
        this.skillTrackerRepository = skillTrackerRepository;
        this.resumeRepository = resumeRepository;
        this.coverLetterRepository = coverLetterRepository;
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        return SecurityUtils.getCurrentUserOrThrow(userRepository);
    }

    public List<JobApplication> getJobApplications() {
        User user = getCurrentUser();
        return jobApplicationRepository.findByUserIdAndDeletedFalse(user.getId(), Pageable.unpaged()).getContent();
    }

    public List<FollowUpReminder> getFollowUpReminders() {
        User user = getCurrentUser();
        return followUpReminderRepository.findByJobApplicationUserIdAndDeletedFalse(user.getId(), Pageable.unpaged()).getContent();
    }

    public List<ScheduledCommunication> getScheduledCommunications() {
        User user = getCurrentUser();
        return scheduledCommunicationRepository.findByJobApplicationUserIdAndDeletedFalse(user.getId(), Pageable.unpaged()).getContent();
    }

    public List<CommunicationLog> getCommunicationLogs() {
        User user = getCurrentUser();
        return communicationLogRepository.findByJobApplicationUserIdAndDeletedFalse(user.getId(), Pageable.unpaged()).getContent();
    }

    public List<Attachment> getAttachments() {
        User user = getCurrentUser();
        return attachmentRepository.findByJobApplicationUserIdAndDeletedFalse(user.getId(), Pageable.unpaged()).getContent();
    }

    public List<ApplicationTimeline> getTimelineEntries() {
        User user = getCurrentUser();
        return applicationTimelineRepository.findByJobApplicationUserIdAndDeletedFalse(user.getId(), Pageable.unpaged()).getContent();
    }

    public List<SkillTracker> getSkills() {
        User user = getCurrentUser();
        return skillTrackerRepository.findByJobApplicationUserIdAndDeletedFalse(user.getId(), Pageable.unpaged()).getContent();
    }

    public List<Resume> getResumes() {
        User user = getCurrentUser();
        return resumeRepository.findByCreatedByAndDeletedFalse(user.getEmail(), Pageable.unpaged()).getContent();
    }

    public List<CoverLetter> getCoverLetters() {
        User user = getCurrentUser();
        return coverLetterRepository.findByCreatedByAndDeletedFalse(user.getEmail(), Pageable.unpaged()).getContent();
    }
}
