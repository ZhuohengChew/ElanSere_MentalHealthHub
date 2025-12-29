package com.mentalhealthhub.dto;

import java.util.List;

public class AppointmentAnalyticsDTO {
    private Long totalAppointments;
    private Long completedAppointments;
    private Long cancelledAppointments;
    private Long scheduledAppointments;
    private Double averageAppointmentsPerStudent;
    private List<ProfessionalWorkloadDTO> professionalWorkloads;
    private List<MonthlyAppointmentDTO> monthlyData;

    public AppointmentAnalyticsDTO() {}

    // Getters and Setters
    public Long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(Long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public Long getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(Long completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public Long getCancelledAppointments() {
        return cancelledAppointments;
    }

    public void setCancelledAppointments(Long cancelledAppointments) {
        this.cancelledAppointments = cancelledAppointments;
    }

    public Long getScheduledAppointments() {
        return scheduledAppointments;
    }

    public void setScheduledAppointments(Long scheduledAppointments) {
        this.scheduledAppointments = scheduledAppointments;
    }

    public Double getAverageAppointmentsPerStudent() {
        return averageAppointmentsPerStudent;
    }

    public void setAverageAppointmentsPerStudent(Double averageAppointmentsPerStudent) {
        this.averageAppointmentsPerStudent = averageAppointmentsPerStudent;
    }

    public List<ProfessionalWorkloadDTO> getProfessionalWorkloads() {
        return professionalWorkloads;
    }

    public void setProfessionalWorkloads(List<ProfessionalWorkloadDTO> professionalWorkloads) {
        this.professionalWorkloads = professionalWorkloads;
    }

    public List<MonthlyAppointmentDTO> getMonthlyData() {
        return monthlyData;
    }

    public void setMonthlyData(List<MonthlyAppointmentDTO> monthlyData) {
        this.monthlyData = monthlyData;
    }

    // Inner class for professional workload
    public static class ProfessionalWorkloadDTO {
        private Long professionalId;
        private String professionalName;
        private Long appointmentCount;

        public ProfessionalWorkloadDTO(Long professionalId, String professionalName, Long appointmentCount) {
            this.professionalId = professionalId;
            this.professionalName = professionalName;
            this.appointmentCount = appointmentCount;
        }

        public Long getProfessionalId() {
            return professionalId;
        }

        public void setProfessionalId(Long professionalId) {
            this.professionalId = professionalId;
        }

        public String getProfessionalName() {
            return professionalName;
        }

        public void setProfessionalName(String professionalName) {
            this.professionalName = professionalName;
        }

        public Long getAppointmentCount() {
            return appointmentCount;
        }

        public void setAppointmentCount(Long appointmentCount) {
            this.appointmentCount = appointmentCount;
        }
    }

    // Inner class for monthly appointments
    public static class MonthlyAppointmentDTO {
        private String month;
        private Long count;

        public MonthlyAppointmentDTO(String month, Long count) {
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
