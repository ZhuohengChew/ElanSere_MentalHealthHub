package com.mentalhealthhub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String status = "pending";

    private String urgency;

    private LocalDateTime submittedAt = LocalDateTime.now();

    // optional assessment scores
    private Integer stressScore;
    private Integer anxietyScore;
    private Integer depressionScore;

    public Report() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public Integer getStressScore() { return stressScore; }
    public void setStressScore(Integer stressScore) { this.stressScore = stressScore; }
    public Integer getAnxietyScore() { return anxietyScore; }
    public void setAnxietyScore(Integer anxietyScore) { this.anxietyScore = anxietyScore; }
    public Integer getDepressionScore() { return depressionScore; }
    public void setDepressionScore(Integer depressionScore) { this.depressionScore = depressionScore; }
}
