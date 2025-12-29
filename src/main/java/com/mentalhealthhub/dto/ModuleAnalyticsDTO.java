package com.mentalhealthhub.dto;

import java.util.List;

public class ModuleAnalyticsDTO {
    private Double averageProgressPercentage;
    private Long totalCompletions;
    private List<ModuleAccessDTO> mostAccessedModules;
    private List<MonthlyCompletionDTO> monthlyCompletions;

    public ModuleAnalyticsDTO() {}

    // Getters and Setters
    public Double getAverageProgressPercentage() {
        return averageProgressPercentage;
    }

    public void setAverageProgressPercentage(Double averageProgressPercentage) {
        this.averageProgressPercentage = averageProgressPercentage;
    }

    public Long getTotalCompletions() {
        return totalCompletions;
    }

    public void setTotalCompletions(Long totalCompletions) {
        this.totalCompletions = totalCompletions;
    }

    public List<ModuleAccessDTO> getMostAccessedModules() {
        return mostAccessedModules;
    }

    public void setMostAccessedModules(List<ModuleAccessDTO> mostAccessedModules) {
        this.mostAccessedModules = mostAccessedModules;
    }

    public List<MonthlyCompletionDTO> getMonthlyCompletions() {
        return monthlyCompletions;
    }

    public void setMonthlyCompletions(List<MonthlyCompletionDTO> monthlyCompletions) {
        this.monthlyCompletions = monthlyCompletions;
    }

    // Inner class for module access
    public static class ModuleAccessDTO {
        private String title;
        private Long completions;
        private Double completionRate;

        public ModuleAccessDTO(String title, Long completions) {
            this.title = title;
            this.completions = completions;
        }

        public ModuleAccessDTO(String title, Long completions, Double completionRate) {
            this.title = title;
            this.completions = completions;
            this.completionRate = completionRate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Long getCompletions() {
            return completions;
        }

        public void setCompletions(Long completions) {
            this.completions = completions;
        }

        public Double getCompletionRate() {
            return completionRate;
        }

        public void setCompletionRate(Double completionRate) {
            this.completionRate = completionRate;
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
