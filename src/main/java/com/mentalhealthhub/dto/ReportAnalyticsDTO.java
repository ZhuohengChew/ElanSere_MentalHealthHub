package com.mentalhealthhub.dto;

import java.util.List;

public class ReportAnalyticsDTO {
    private Long totalReports;
    private Long pendingReports;
    private Long inProgressReports;
    private Long resolvedReports;
    private Long closedReports;
    private Long lowUrgency;
    private Long mediumUrgency;
    private Long highUrgency;
    private Long criticalUrgency;
    private Double averageResolutionTimeHours;
    private List<ReportTypeDTO> reportsByType;
    private List<MonthlyReportDTO> monthlyTrend;
    private List<MonthlyResolutionDTO> monthlyResolutions;

    public ReportAnalyticsDTO() {}

    // Getters and Setters
    public Long getTotalReports() {
        return totalReports;
    }

    public void setTotalReports(Long totalReports) {
        this.totalReports = totalReports;
    }

    public Long getPendingReports() {
        return pendingReports;
    }

    public void setPendingReports(Long pendingReports) {
        this.pendingReports = pendingReports;
    }

    public Long getInProgressReports() {
        return inProgressReports;
    }

    public void setInProgressReports(Long inProgressReports) {
        this.inProgressReports = inProgressReports;
    }

    public Long getResolvedReports() {
        return resolvedReports;
    }

    public void setResolvedReports(Long resolvedReports) {
        this.resolvedReports = resolvedReports;
    }

    public Long getClosedReports() {
        return closedReports;
    }

    public void setClosedReports(Long closedReports) {
        this.closedReports = closedReports;
    }

    public Long getLowUrgency() {
        return lowUrgency;
    }

    public void setLowUrgency(Long lowUrgency) {
        this.lowUrgency = lowUrgency;
    }

    public Long getMediumUrgency() {
        return mediumUrgency;
    }

    public void setMediumUrgency(Long mediumUrgency) {
        this.mediumUrgency = mediumUrgency;
    }

    public Long getHighUrgency() {
        return highUrgency;
    }

    public void setHighUrgency(Long highUrgency) {
        this.highUrgency = highUrgency;
    }

    public Long getCriticalUrgency() {
        return criticalUrgency;
    }

    public void setCriticalUrgency(Long criticalUrgency) {
        this.criticalUrgency = criticalUrgency;
    }

    public Double getAverageResolutionTimeHours() {
        return averageResolutionTimeHours;
    }

    public void setAverageResolutionTimeHours(Double averageResolutionTimeHours) {
        this.averageResolutionTimeHours = averageResolutionTimeHours;
    }

    public List<ReportTypeDTO> getReportsByType() {
        return reportsByType;
    }

    public void setReportsByType(List<ReportTypeDTO> reportsByType) {
        this.reportsByType = reportsByType;
    }

    public List<MonthlyReportDTO> getMonthlyTrend() {
        return monthlyTrend;
    }

    public void setMonthlyTrend(List<MonthlyReportDTO> monthlyTrend) {
        this.monthlyTrend = monthlyTrend;
    }

    public List<MonthlyResolutionDTO> getMonthlyResolutions() {
        return monthlyResolutions;
    }

    public void setMonthlyResolutions(List<MonthlyResolutionDTO> monthlyResolutions) {
        this.monthlyResolutions = monthlyResolutions;
    }

    // Inner class for report type
    public static class ReportTypeDTO {
        private String type;
        private Long count;

        public ReportTypeDTO(String type, Long count) {
            this.type = type;
            this.count = count;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    // Inner class for monthly report
    public static class MonthlyReportDTO {
        private String month;
        private Long count;

        public MonthlyReportDTO(String month, Long count) {
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

    // Inner class for monthly resolution
    public static class MonthlyResolutionDTO {
        private String month;
        private Long count;

        public MonthlyResolutionDTO(String month, Long count) {
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
