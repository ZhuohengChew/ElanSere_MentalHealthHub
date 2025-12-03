package com.mentalhealthhub.model;

public enum UserRole {
    STUDENT("Student"),
    STAFF("UTM Staff"),
    PROFESSIONAL("Mental Health Professional"),
    ADMIN("Administrator");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
