package com.mentalhealthhub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessments")
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Legacy fields (for backward compatibility)
    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String content;

    // New structured assessment fields
    @Column(nullable = false)
    private LocalDateTime completedAt;

    // Store answers as comma-separated values (e.g., "2,1,3,2,1,0,2,3")
    @Column(nullable = false)
    private String answers;

    @Column(nullable = false)
    private Integer totalScore;

    @Column(nullable = false)
    private String category; // LOW, MILD, MODERATE, SEVERE

    // Individual question scores
    private Integer q1Score;
    private Integer q2Score;
    private Integer q3Score;
    private Integer q4Score;
    private Integer q5Score;
    private Integer q6Score;
    private Integer q7Score;
    private Integer q8Score;

    // Legacy fields
    private Integer score;
    private String result;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (completedAt == null) {
            completedAt = LocalDateTime.now();
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

    public Assessment() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getQ1Score() {
        return q1Score;
    }

    public void setQ1Score(Integer q1Score) {
        this.q1Score = q1Score;
    }

    public Integer getQ2Score() {
        return q2Score;
    }

    public void setQ2Score(Integer q2Score) {
        this.q2Score = q2Score;
    }

    public Integer getQ3Score() {
        return q3Score;
    }

    public void setQ3Score(Integer q3Score) {
        this.q3Score = q3Score;
    }

    public Integer getQ4Score() {
        return q4Score;
    }

    public void setQ4Score(Integer q4Score) {
        this.q4Score = q4Score;
    }

    public Integer getQ5Score() {
        return q5Score;
    }

    public void setQ5Score(Integer q5Score) {
        this.q5Score = q5Score;
    }

    public Integer getQ6Score() {
        return q6Score;
    }

    public void setQ6Score(Integer q6Score) {
        this.q6Score = q6Score;
    }

    public Integer getQ7Score() {
        return q7Score;
    }

    public void setQ7Score(Integer q7Score) {
        this.q7Score = q7Score;
    }

    public Integer getQ8Score() {
        return q8Score;
    }

    public void setQ8Score(Integer q8Score) {
        this.q8Score = q8Score;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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
