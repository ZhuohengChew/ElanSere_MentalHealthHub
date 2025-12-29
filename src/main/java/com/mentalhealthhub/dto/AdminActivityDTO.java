package com.mentalhealthhub.dto;

import java.util.List;

public class AdminActivityDTO {
    private Long totalActionsThisWeek;
    private List<ActionCountDTO> commonActions;
    private List<AdminActivityCountDTO> adminActivity;

    public AdminActivityDTO() {}

    // Getters and Setters
    public Long getTotalActionsThisWeek() {
        return totalActionsThisWeek;
    }

    public void setTotalActionsThisWeek(Long totalActionsThisWeek) {
        this.totalActionsThisWeek = totalActionsThisWeek;
    }

    public List<ActionCountDTO> getCommonActions() {
        return commonActions;
    }

    public void setCommonActions(List<ActionCountDTO> commonActions) {
        this.commonActions = commonActions;
    }

    public List<AdminActivityCountDTO> getAdminActivity() {
        return adminActivity;
    }

    public void setAdminActivity(List<AdminActivityCountDTO> adminActivity) {
        this.adminActivity = adminActivity;
    }

    // Inner class for action count
    public static class ActionCountDTO {
        private String action;
        private Long count;

        public ActionCountDTO(String action, Long count) {
            this.action = action;
            this.count = count;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    // Inner class for admin activity count
    public static class AdminActivityCountDTO {
        private Long adminId;
        private String adminName;
        private Long actionCount;

        public AdminActivityCountDTO(Long adminId, String adminName, Long actionCount) {
            this.adminId = adminId;
            this.adminName = adminName;
            this.actionCount = actionCount;
        }

        public Long getAdminId() {
            return adminId;
        }

        public void setAdminId(Long adminId) {
            this.adminId = adminId;
        }

        public String getAdminName() {
            return adminName;
        }

        public void setAdminName(String adminName) {
            this.adminName = adminName;
        }

        public Long getActionCount() {
            return actionCount;
        }

        public void setActionCount(Long actionCount) {
            this.actionCount = actionCount;
        }
    }
}
