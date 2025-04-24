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
@Table(name = "communication_log")
public class CommunicationLog {

    public enum Method {
        EMAIL, CALL, LINKEDIN, IN_PERSON;
        
        public String getName() {
            return this.name().toUpperCase();
        }
    }

    public enum Direction {
        INBOUND, OUTBOUND;
        
        public String getName() {
            return this.name().toUpperCase();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Method type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Direction direction;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String message;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_application_id")
    private JobApplication jobApplication;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Method getType() {
        return type;
    }

    public void setType(Method type) {
        this.type = type;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JobApplication getJobApplication() {
        return jobApplication;
    }

    public void setJobApplication(JobApplication jobApplication) {
        this.jobApplication = jobApplication;
    }
}
