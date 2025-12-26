package com.mentalhealthhub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long performedBy;

    @Column(nullable = false)
    private String action;
    // USER_CREATED, USER_UPDATED, USER_ACTIVATED, USER_DEACTIVATED, USER_ARCHIVED, PASSWORD_RESET

    @Column(name = "user_id")
    private Long userId;

    @Column(columnDefinition = "LONGTEXT")
    private String details;
    // JSON format: {"field": "old_value -> new_value", ...}

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public AuditLog() {
    }

    public AuditLog(Long performedBy, String action, Long userId, String details) {
        this.performedBy = performedBy;
        this.action = action;
        this.userId = userId;
        this.details = details;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(Long performedBy) {
        this.performedBy = performedBy;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
