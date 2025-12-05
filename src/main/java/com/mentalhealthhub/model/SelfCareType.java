package com.mentalhealthhub.model;

public enum SelfCareType {
    MOOD("Mood Tracking"),
    MEDITATION("Meditation"),
    BREATHING("Breathing Exercise"),
    EXERCISE("Physical Exercise"),
    MUSIC("Music/Relaxation");

    private final String displayName;

    SelfCareType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

