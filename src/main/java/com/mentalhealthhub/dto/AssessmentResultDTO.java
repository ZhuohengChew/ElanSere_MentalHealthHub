package com.mentalhealthhub.dto;

public class AssessmentResultDTO {
    private Integer totalScore;
    private String category;
    private String message;
    private String recommendation;
    private Integer stressLevel;
    private Integer anxietyLevel;
    private Integer depressionLevel;

    public AssessmentResultDTO() {
    }

    public AssessmentResultDTO(Integer totalScore, String category, String message,
            String recommendation, Integer stressLevel,
            Integer anxietyLevel, Integer depressionLevel) {
        this.totalScore = totalScore;
        this.category = category;
        this.message = message;
        this.recommendation = recommendation;
        this.stressLevel = stressLevel;
        this.anxietyLevel = anxietyLevel;
        this.depressionLevel = depressionLevel;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public Integer getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(Integer stressLevel) {
        this.stressLevel = stressLevel;
    }

    public Integer getAnxietyLevel() {
        return anxietyLevel;
    }

    public void setAnxietyLevel(Integer anxietyLevel) {
        this.anxietyLevel = anxietyLevel;
    }

    public Integer getDepressionLevel() {
        return depressionLevel;
    }

    public void setDepressionLevel(Integer depressionLevel) {
        this.depressionLevel = depressionLevel;
    }
}
