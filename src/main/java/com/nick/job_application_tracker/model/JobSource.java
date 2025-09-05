package com.nick.job_application_tracker.model;

import com.nick.job_application_tracker.model.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a source where a job posting was found (e.g., LinkedIn, Referral).
 */
@Entity
@Table(name = "job_source")
public class JobSource extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    // --- Constructors ---

    public JobSource() {}

    public JobSource(String name) {
        this.name = name;
    }

    // --- Lifecycle Hooks ---

    @PrePersist
    public void prePersist() {
        name = capitalizeWords(name.trim());
    }

    private String capitalizeWords(String input) {
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isBlank()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1)).append(" ");
            }
        }
        return result.toString().trim();
    }

    // --- Getters and Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
