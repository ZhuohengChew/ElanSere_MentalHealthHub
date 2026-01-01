package com.mentalhealthhub.dto;

public class ComprehensiveAnalyticsDTO {
    private UserAnalyticsDTO userAnalytics;
    private MentalHealthTrendsDTO mentalHealthTrends;
    private AppointmentAnalyticsDTO appointmentAnalytics;
    private ModuleAnalyticsDTO moduleAnalytics;
    private SelfCareAnalyticsDTO selfCareAnalytics;
    private ForumAnalyticsDTO forumAnalytics;
    private ReportAnalyticsDTO reportAnalytics;
    private AdminActivityDTO adminActivity;
    private java.util.Map<Long, Double> engagementRates;
    private java.util.List<com.mentalhealthhub.dto.EngagementRateDTO> engagementList;

    public ComprehensiveAnalyticsDTO() {}

    // Getters and Setters
    public UserAnalyticsDTO getUserAnalytics() {
        return userAnalytics;
    }

    public void setUserAnalytics(UserAnalyticsDTO userAnalytics) {
        this.userAnalytics = userAnalytics;
    }

    public MentalHealthTrendsDTO getMentalHealthTrends() {
        return mentalHealthTrends;
    }

    public void setMentalHealthTrends(MentalHealthTrendsDTO mentalHealthTrends) {
        this.mentalHealthTrends = mentalHealthTrends;
    }

    public AppointmentAnalyticsDTO getAppointmentAnalytics() {
        return appointmentAnalytics;
    }

    public void setAppointmentAnalytics(AppointmentAnalyticsDTO appointmentAnalytics) {
        this.appointmentAnalytics = appointmentAnalytics;
    }

    public ModuleAnalyticsDTO getModuleAnalytics() {
        return moduleAnalytics;
    }

    public void setModuleAnalytics(ModuleAnalyticsDTO moduleAnalytics) {
        this.moduleAnalytics = moduleAnalytics;
    }

    public SelfCareAnalyticsDTO getSelfCareAnalytics() {
        return selfCareAnalytics;
    }

    public void setSelfCareAnalytics(SelfCareAnalyticsDTO selfCareAnalytics) {
        this.selfCareAnalytics = selfCareAnalytics;
    }

    public ForumAnalyticsDTO getForumAnalytics() {
        return forumAnalytics;
    }

    public void setForumAnalytics(ForumAnalyticsDTO forumAnalytics) {
        this.forumAnalytics = forumAnalytics;
    }

    public ReportAnalyticsDTO getReportAnalytics() {
        return reportAnalytics;
    }

    public void setReportAnalytics(ReportAnalyticsDTO reportAnalytics) {
        this.reportAnalytics = reportAnalytics;
    }

    public AdminActivityDTO getAdminActivity() {
        return adminActivity;
    }

    public void setAdminActivity(AdminActivityDTO adminActivity) {
        this.adminActivity = adminActivity;
    }

    public java.util.Map<Long, Double> getEngagementRates() {
        return engagementRates;
    }

    public void setEngagementRates(java.util.Map<Long, Double> engagementRates) {
        this.engagementRates = engagementRates;
    }

    public java.util.List<com.mentalhealthhub.dto.EngagementRateDTO> getEngagementList() {
        return engagementList;
    }

    public void setEngagementList(java.util.List<com.mentalhealthhub.dto.EngagementRateDTO> engagementList) {
        this.engagementList = engagementList;
    }
}
