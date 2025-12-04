package com.mentalhealthhub.dto;

public class AssessmentQuestionDTO {
    private int questionNumber;
    private String questionText;
    private String category; // STRESS, ANXIETY, DEPRESSION, SLEEP

    public AssessmentQuestionDTO() {
    }

    public AssessmentQuestionDTO(int questionNumber, String questionText, String category) {
        this.questionNumber = questionNumber;
        this.questionText = questionText;
        this.category = category;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
