package com.mentalhealthhub.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "self_care")
public class SelfCare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SelfCareType type; // MOOD, MEDITATION, BREATHING, EXERCISE, MUSIC

    @Column(nullable = false)
    private LocalDate activityDate;

    // For mood tracking
    @Column
    private String mood; // great, good, okay, low, struggling

    // For meditation/exercise/music sessions
    @Column
    private String activityTitle; // e.g., "Morning Mindfulness", "Progressive Muscle Relaxation"

    @Column
    private Integer durationMinutes; // Duration in minutes

    @Column(columnDefinition = "TEXT")
    private String notes; // Optional notes or description

    // For breathing exercises
    @Column
    private Integer breathDuration; // Breath duration in seconds

    @Column
    private String breathingTechnique; // e.g., "Box Breathing", "4-7-8 Breathing", "Belly Breathing"

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (activityDate == null) {
            activityDate = LocalDate.now();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public SelfCare() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SelfCareType getType() {
        return type;
    }

    public void setType(SelfCareType type) {
        this.type = type;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getBreathDuration() {
        return breathDuration;
    }

    public void setBreathDuration(Integer breathDuration) {
        this.breathDuration = breathDuration;
    }

    public String getBreathingTechnique() {
        return breathingTechnique;
    }

    public void setBreathingTechnique(String breathingTechnique) {
        this.breathingTechnique = breathingTechnique;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

