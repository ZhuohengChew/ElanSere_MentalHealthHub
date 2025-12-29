package com.mentalhealthhub.dto;

import java.util.List;
import java.util.Map;

public class SelfCareAnalyticsDTO {
    private List<ActivityCountDTO> activityDistribution;
    private Map<String, Long> moodDistribution;
    private List<DailyFrequencyDTO> dailyFrequency;
    private List<WeeklyFrequencyDTO> weeklyFrequency;
    private List<MonthlyFrequencyDTO> monthlyFrequency;

    public SelfCareAnalyticsDTO() {}

    // Getters and Setters
    public List<ActivityCountDTO> getActivityDistribution() {
        return activityDistribution;
    }

    public void setActivityDistribution(List<ActivityCountDTO> activityDistribution) {
        this.activityDistribution = activityDistribution;
    }

    public Map<String, Long> getMoodDistribution() {
        return moodDistribution;
    }

    public void setMoodDistribution(Map<String, Long> moodDistribution) {
        this.moodDistribution = moodDistribution;
    }

    public List<DailyFrequencyDTO> getDailyFrequency() {
        return dailyFrequency;
    }

    public void setDailyFrequency(List<DailyFrequencyDTO> dailyFrequency) {
        this.dailyFrequency = dailyFrequency;
    }

    public List<WeeklyFrequencyDTO> getWeeklyFrequency() {
        return weeklyFrequency;
    }

    public void setWeeklyFrequency(List<WeeklyFrequencyDTO> weeklyFrequency) {
        this.weeklyFrequency = weeklyFrequency;
    }

    public List<MonthlyFrequencyDTO> getMonthlyFrequency() {
        return monthlyFrequency;
    }

    public void setMonthlyFrequency(List<MonthlyFrequencyDTO> monthlyFrequency) {
        this.monthlyFrequency = monthlyFrequency;
    }

    // Inner class for activity count
    public static class ActivityCountDTO {
        private String activity;
        private Long count;

        public ActivityCountDTO(String activity, Long count) {
            this.activity = activity;
            this.count = count;
        }

        public String getActivity() {
            return activity;
        }

        public void setActivity(String activity) {
            this.activity = activity;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    // Inner class for daily frequency
    public static class DailyFrequencyDTO {
        private String date;
        private Long count;

        public DailyFrequencyDTO(String date, Long count) {
            this.date = date;
            this.count = count;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    // Inner class for weekly frequency
    public static class WeeklyFrequencyDTO {
        private String week;
        private Long count;

        public WeeklyFrequencyDTO(String week, Long count) {
            this.week = week;
            this.count = count;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    // Inner class for monthly frequency
    public static class MonthlyFrequencyDTO {
        private String month;
        private Long count;

        public MonthlyFrequencyDTO(String month, Long count) {
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
