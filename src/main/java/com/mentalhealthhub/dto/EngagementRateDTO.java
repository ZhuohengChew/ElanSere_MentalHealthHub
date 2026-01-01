package com.mentalhealthhub.dto;

public class EngagementRateDTO {
    private Long userId;
    private String userName;
    private Double rate;

    public EngagementRateDTO() {}

    public EngagementRateDTO(Long userId, String userName, Double rate) {
        this.userId = userId;
        this.userName = userName;
        this.rate = rate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
