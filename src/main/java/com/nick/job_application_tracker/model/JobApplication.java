package com.nick.job_application_tracker.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_application")
public class JobApplication {

    public enum Status {
        APPLIED, INTERVIEW, OFFER, REJECTED;
        
        public String getName() {
            return this.name().toUpperCase();
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String jobTitle;

    @Column(nullable = false)
    private String company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "source_id")
    private JobSource source;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @ManyToOne
    @JoinColumn(name = "cover_letter_id")
    private CoverLetter coverLetter;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime appliedOn;

    public JobApplication(Long id, String jobTitle, String company, Status status, User user, Location location,
            JobSource source, Resume resume, CoverLetter coverLetter, String notes, LocalDateTime appliedOn) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.company = company;
        this.status = status;
        this.user = user;
        this.location = location;
        this.source = source;
        this.resume = resume;
        this.coverLetter = coverLetter;
        this.notes = notes;
        this.appliedOn = appliedOn;
    }
    
    public JobApplication() {}

    // Getters and Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public JobSource getSource() {
        return source;
    }

    public void setSource(JobSource source) {
        this.source = source;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public CoverLetter getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(CoverLetter coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getAppliedOn() {
        return appliedOn;
    }

    public void setAppliedOn(LocalDateTime appliedOn) {
        this.appliedOn = appliedOn;
    }
}
