package com.mentalhealthhub.dto;

import java.util.List;

public class MentalHealthTrendsDTO {
    private Double averageStressLevel;
    private Double averageAnxietyLevel;
    private Double averageWellbeingScore;
    
    // Distribution by score ranges
    private Long lowScore;        // 0-20
    private Long mildScore;       // 21-40
    private Long moderateScore;   // 41-60
    private Long highScore;       // 61-100
    
    private List<MonthlyTrendDTO> trendData;
    private List<MonthlyCompletionDTO> monthlyCompletions;
    private Long totalAssessments;  // Total number of assessments completed

    public MentalHealthTrendsDTO() {}

    // Getters and Setters
    public Double getAverageStressLevel() {
        return averageStressLevel;
    }

    public void setAverageStressLevel(Double averageStressLevel) {
        this.averageStressLevel = averageStressLevel;
    }

    public Double getAverageAnxietyLevel() {
        return averageAnxietyLevel;
    }

    public void setAverageAnxietyLevel(Double averageAnxietyLevel) {
        this.averageAnxietyLevel = averageAnxietyLevel;
    }

    public Double getAverageWellbeingScore() {
        return averageWellbeingScore;
    }

    public void setAverageWellbeingScore(Double averageWellbeingScore) {
        this.averageWellbeingScore = averageWellbeingScore;
    }

    public Long getLowScore() {
        return lowScore;
    }

    public void setLowScore(Long lowScore) {
        this.lowScore = lowScore;
    }

    public Long getMildScore() {
        return mildScore;
    }

    public void setMildScore(Long mildScore) {
        this.mildScore = mildScore;
    }

    public Long getModerateScore() {
        return moderateScore;
    }

    public void setModerateScore(Long moderateScore) {
        this.moderateScore = moderateScore;
    }

    public Long getHighScore() {
        return highScore;
    }

    public void setHighScore(Long highScore) {
        this.highScore = highScore;
    }

    public List<MonthlyTrendDTO> getTrendData() {
        return trendData;
    }

    public void setTrendData(List<MonthlyTrendDTO> trendData) {
        this.trendData = trendData;
    }

    public List<MonthlyCompletionDTO> getMonthlyCompletions() {
        return monthlyCompletions;
    }

    public void setMonthlyCompletions(List<MonthlyCompletionDTO> monthlyCompletions) {
        this.monthlyCompletions = monthlyCompletions;
    }

    public Long getTotalAssessments() {
        return totalAssessments;
    }

    public void setTotalAssessments(Long totalAssessments) {
        this.totalAssessments = totalAssessments;
    }

    // Inner class for monthly trends
    public static class MonthlyTrendDTO {
        private String month;
        private Double averageScore;

        public MonthlyTrendDTO(String month, Double averageScore) {
            this.month = month;
            this.averageScore = averageScore;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public Double getAverageScore() {
            return averageScore;
        }

        public void setAverageScore(Double averageScore) {
            this.averageScore = averageScore;
        }
    }

    // Inner class for monthly completions
    public static class MonthlyCompletionDTO {
        private String month;
        private Long count;

        public MonthlyCompletionDTO(String month, Long count) {
            this.month = month;
            this.count = count;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
}
