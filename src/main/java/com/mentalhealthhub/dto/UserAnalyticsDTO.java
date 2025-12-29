package com.mentalhealthhub.dto;

public class UserAnalyticsDTO {
    private Long totalUsers;
    private Long activeUsers;
    private Long inactiveUsers;
    private Long studentsCount;
    private Long staffCount;
    private Long professionalsCount;
    private Long adminsCount;
    private Long newUsersLast7Days;
    private Long newUsersLast30Days;
    private Double averageStressLevel;
    private Double averageAnxietyLevel;
    private Double averageWellbeingScore;

    public UserAnalyticsDTO() {}

    public UserAnalyticsDTO(Long totalUsers, Long activeUsers, Long inactiveUsers) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.inactiveUsers = inactiveUsers;
    }

    // Getters and Setters
    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Long getInactiveUsers() {
        return inactiveUsers;
    }

    public void setInactiveUsers(Long inactiveUsers) {
        this.inactiveUsers = inactiveUsers;
    }

    public Long getStudentsCount() {
        return studentsCount;
    }

    public void setStudentsCount(Long studentsCount) {
        this.studentsCount = studentsCount;
    }

    public Long getStaffCount() {
        return staffCount;
    }

    public void setStaffCount(Long staffCount) {
        this.staffCount = staffCount;
    }

    public Long getProfessionalsCount() {
        return professionalsCount;
    }

    public void setProfessionalsCount(Long professionalsCount) {
        this.professionalsCount = professionalsCount;
    }

    public Long getAdminsCount() {
        return adminsCount;
    }

    public void setAdminsCount(Long adminsCount) {
        this.adminsCount = adminsCount;
    }

    public Long getNewUsersLast7Days() {
        return newUsersLast7Days;
    }

    public void setNewUsersLast7Days(Long newUsersLast7Days) {
        this.newUsersLast7Days = newUsersLast7Days;
    }

    public Long getNewUsersLast30Days() {
        return newUsersLast30Days;
    }

    public void setNewUsersLast30Days(Long newUsersLast30Days) {
        this.newUsersLast30Days = newUsersLast30Days;
    }

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
}
