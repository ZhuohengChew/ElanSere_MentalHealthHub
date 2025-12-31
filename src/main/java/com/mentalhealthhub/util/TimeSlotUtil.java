package com.mentalhealthhub.util;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TimeSlotUtil {
    
    // Time slot duration in minutes
    public static final int SLOT_DURATION_MINUTES = 30;
    
    // Morning start and end times
    public static final LocalTime MORNING_START = LocalTime.of(8, 0);
    public static final LocalTime MORNING_END = LocalTime.of(13, 0);
    
    // Afternoon start and end times (after lunch break at 1:00 PM - 2:00 PM)
    public static final LocalTime AFTERNOON_START = LocalTime.of(14, 0);
    public static final LocalTime AFTERNOON_END = LocalTime.of(17, 0);
    
    /**
     * Generates all available time slots for a given day
     * Morning: 8:00-13:00, Afternoon: 14:00-17:00
     * Each slot is 30 minutes
     * Lunch break: 13:00-14:00
     */
    public static List<TimeSlot> generateAllTimeSlots() {
        List<TimeSlot> slots = new ArrayList<>();
        
        // Generate morning slots
        LocalTime currentTime = MORNING_START;
        while (currentTime.isBefore(MORNING_END)) {
            LocalTime slotEnd = currentTime.plusMinutes(SLOT_DURATION_MINUTES);
            slots.add(new TimeSlot(currentTime, slotEnd));
            currentTime = slotEnd;
        }
        
        // Generate afternoon slots (after lunch break)
        currentTime = AFTERNOON_START;
        while (currentTime.isBefore(AFTERNOON_END)) {
            LocalTime slotEnd = currentTime.plusMinutes(SLOT_DURATION_MINUTES);
            slots.add(new TimeSlot(currentTime, slotEnd));
            currentTime = slotEnd;
        }
        
        return slots;
    }
    
    /**
     * Validates if a list of selected time slots are continuous (no gaps)
     * @param selectedSlots List of selected time slots
     * @return true if slots are continuous, false otherwise
     */
    public static boolean areSlotsConsecutive(List<TimeSlot> selectedSlots) {
        if (selectedSlots == null || selectedSlots.isEmpty()) {
            return false;
        }
        
        if (selectedSlots.size() == 1) {
            return true;
        }
        
        // Sort slots by start time
        List<TimeSlot> sortedSlots = selectedSlots.stream()
            .sorted((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()))
            .collect(Collectors.toList());
        
        // Check if each slot's end time equals next slot's start time
        for (int i = 0; i < sortedSlots.size() - 1; i++) {
            LocalTime currentEnd = sortedSlots.get(i).getEndTime();
            LocalTime nextStart = sortedSlots.get(i + 1).getStartTime();
            
            if (!currentEnd.equals(nextStart)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validates if slots cross the lunch break boundary (1:00 PM - 2:00 PM)
     * @param selectedSlots List of selected time slots
     * @return true if slots are valid (don't cross lunch break), false otherwise
     */
    public static boolean slotsValidateLunchBreak(List<TimeSlot> selectedSlots) {
        if (selectedSlots == null || selectedSlots.isEmpty()) {
            return true;
        }
        
        LocalTime lunchStart = LocalTime.of(13, 0);
        LocalTime lunchEnd = LocalTime.of(14, 0);
        
        for (TimeSlot slot : selectedSlots) {
            // Check if slot is during lunch break
            if (slot.getStartTime().isBefore(lunchEnd) && slot.getEndTime().isAfter(lunchStart)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Gets the total duration of selected slots in minutes
     * @param selectedSlots List of selected time slots
     * @return Total duration in minutes
     */
    public static int getTotalDurationMinutes(List<TimeSlot> selectedSlots) {
        if (selectedSlots == null || selectedSlots.isEmpty()) {
            return 0;
        }
        
        return selectedSlots.size() * SLOT_DURATION_MINUTES;
    }
    
    /**
     * Gets the earliest start time from a list of slots
     * @param selectedSlots List of selected time slots
     * @return Earliest start time, or null if empty
     */
    public static LocalTime getEarliestStartTime(List<TimeSlot> selectedSlots) {
        if (selectedSlots == null || selectedSlots.isEmpty()) {
            return null;
        }
        
        return selectedSlots.stream()
            .map(TimeSlot::getStartTime)
            .min(LocalTime::compareTo)
            .orElse(null);
    }
    
    /**
     * Gets the latest end time from a list of slots
     * @param selectedSlots List of selected time slots
     * @return Latest end time, or null if empty
     */
    public static LocalTime getLatestEndTime(List<TimeSlot> selectedSlots) {
        if (selectedSlots == null || selectedSlots.isEmpty()) {
            return null;
        }
        
        return selectedSlots.stream()
            .map(TimeSlot::getEndTime)
            .max(LocalTime::compareTo)
            .orElse(null);
    }
    
    /**
     * Inner class representing a time slot
     */
    public static class TimeSlot {
        private LocalTime startTime;
        private LocalTime endTime;
        
        public TimeSlot(LocalTime startTime, LocalTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        public LocalTime getStartTime() {
            return startTime;
        }
        
        public void setStartTime(LocalTime startTime) {
            this.startTime = startTime;
        }
        
        public LocalTime getEndTime() {
            return endTime;
        }
        
        public void setEndTime(LocalTime endTime) {
            this.endTime = endTime;
        }
        
        @Override
        public String toString() {
            return String.format("%02d:%02d-%02d:%02d", 
                startTime.getHour(), startTime.getMinute(),
                endTime.getHour(), endTime.getMinute());
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TimeSlot timeSlot = (TimeSlot) o;
            return startTime.equals(timeSlot.startTime) &&
                   endTime.equals(timeSlot.endTime);
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(startTime, endTime);
        }
    }
}
