package com.mentalhealthhub.dto;

public class StaffDashboardMetricsDTO {
    private long totalStudents;
    private long unresolvedReports;
    private long assessmentsDone;
    private double averageWellnessScore;
    private double averageAnxietyLevel;
    private double averageStressLevel;

    public StaffDashboardMetricsDTO() {
    }

    public StaffDashboardMetricsDTO(long totalStudents, long unresolvedReports, long assessmentsDone,
                                   double averageWellnessScore, double averageAnxietyLevel, 
                                   double averageStressLevel) {
        this.totalStudents = totalStudents;
        this.unresolvedReports = unresolvedReports;
        this.assessmentsDone = assessmentsDone;
        this.averageWellnessScore = averageWellnessScore;
        this.averageAnxietyLevel = averageAnxietyLevel;
        this.averageStressLevel = averageStressLevel;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getUnresolvedReports() {
        return unresolvedReports;
    }

    public void setUnresolvedReports(long unresolvedReports) {
        this.unresolvedReports = unresolvedReports;
    }

    public long getAssessmentsDone() {
        return assessmentsDone;
    }

    public void setAssessmentsDone(long assessmentsDone) {
        this.assessmentsDone = assessmentsDone;
    }

    public double getAverageWellnessScore() {
        return averageWellnessScore;
    }

    public void setAverageWellnessScore(double averageWellnessScore) {
        this.averageWellnessScore = averageWellnessScore;
    }

    public double getAverageAnxietyLevel() {
        return averageAnxietyLevel;
    }

    public void setAverageAnxietyLevel(double averageAnxietyLevel) {
        this.averageAnxietyLevel = averageAnxietyLevel;
    }

    public double getAverageStressLevel() {
        return averageStressLevel;
    }

    public void setAverageStressLevel(double averageStressLevel) {
        this.averageStressLevel = averageStressLevel;
    }
}
