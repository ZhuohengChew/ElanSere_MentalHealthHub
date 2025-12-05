package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.SelfCare;
import com.mentalhealthhub.model.SelfCareType;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.SelfCareRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/self-care")
public class SelfCareController {

    @Autowired
    private SelfCareRepository selfCareRepository;

    @GetMapping
    public String selfCarePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Optimized: Only get mood history for the past week (more efficient)
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        LocalDate today = LocalDate.now();
        
        // Optimized query: Filter by type at database level
        List<SelfCare> recentMoods = selfCareRepository.findByUserAndTypeAndActivityDateBetween(
            user, SelfCareType.MOOD, weekAgo, today
        );

        // Check if mood was logged today
        SelfCare todayMood = selfCareRepository.findByUserAndTypeAndActivityDate(user, SelfCareType.MOOD, today)
            .stream()
            .findFirst()
            .orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("moodHistory", recentMoods);
        model.addAttribute("todayMood", todayMood);
        model.addAttribute("page", "student/self-care");
        model.addAttribute("title", "Self-Care Tools");
        model.addAttribute("activePage", "self-care");

        return "layout";
    }

    @PostMapping("/mood")
    public String saveMood(@RequestParam String mood, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Check if mood already logged today - optimized query
        LocalDate today = LocalDate.now();
        List<SelfCare> todayMoods = selfCareRepository.findByUserAndTypeAndActivityDate(user, SelfCareType.MOOD, today);

        SelfCare selfCare;
        if (!todayMoods.isEmpty()) {
            // Update existing mood entry
            selfCare = todayMoods.get(0);
            selfCare.setMood(mood);
            selfCare.setUpdatedAt(LocalDateTime.now());
        } else {
            // Create new mood entry
            selfCare = new SelfCare();
            selfCare.setUser(user);
            selfCare.setType(SelfCareType.MOOD);
            selfCare.setActivityDate(today);
            selfCare.setMood(mood);
            selfCare.setCreatedAt(LocalDateTime.now());
            selfCare.setUpdatedAt(LocalDateTime.now());
        }

        selfCareRepository.save(selfCare);
        redirectAttributes.addFlashAttribute("success", "Mood logged successfully!");
        redirectAttributes.addFlashAttribute("activeTab", "mood");
        
        return "redirect:/self-care";
    }

    @PostMapping("/meditation")
    public String saveMeditation(
            @RequestParam String title,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        SelfCare selfCare = new SelfCare();
        selfCare.setUser(user);
        selfCare.setType(SelfCareType.MEDITATION);
        selfCare.setActivityDate(LocalDate.now());
        selfCare.setActivityTitle(title);
        selfCare.setDurationMinutes(duration);
        selfCare.setNotes(notes);
        selfCare.setCreatedAt(LocalDateTime.now());
        selfCare.setUpdatedAt(LocalDateTime.now());

        selfCareRepository.save(selfCare);
        redirectAttributes.addFlashAttribute("success", "Meditation session saved!");
        
        return "redirect:/self-care";
    }

    @PostMapping("/breathing")
    public String saveBreathing(
            @RequestParam String technique,
            @RequestParam(required = false) Integer breathDuration,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        SelfCare selfCare = new SelfCare();
        selfCare.setUser(user);
        selfCare.setType(SelfCareType.BREATHING);
        selfCare.setActivityDate(LocalDate.now());
        selfCare.setBreathingTechnique(technique);
        selfCare.setBreathDuration(breathDuration);
        selfCare.setDurationMinutes(duration);
        selfCare.setNotes(notes);
        selfCare.setCreatedAt(LocalDateTime.now());
        selfCare.setUpdatedAt(LocalDateTime.now());

        selfCareRepository.save(selfCare);
        redirectAttributes.addFlashAttribute("success", "Breathing exercise saved!");
        
        return "redirect:/self-care";
    }

    @PostMapping("/exercise")
    public String saveExercise(
            @RequestParam String title,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        SelfCare selfCare = new SelfCare();
        selfCare.setUser(user);
        selfCare.setType(SelfCareType.EXERCISE);
        selfCare.setActivityDate(LocalDate.now());
        selfCare.setActivityTitle(title);
        selfCare.setDurationMinutes(duration);
        selfCare.setNotes(notes);
        selfCare.setCreatedAt(LocalDateTime.now());
        selfCare.setUpdatedAt(LocalDateTime.now());

        selfCareRepository.save(selfCare);
        redirectAttributes.addFlashAttribute("success", "Exercise session saved!");
        
        return "redirect:/self-care";
    }

    @PostMapping("/music")
    public String saveMusic(
            @RequestParam String title,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        SelfCare selfCare = new SelfCare();
        selfCare.setUser(user);
        selfCare.setType(SelfCareType.MUSIC);
        selfCare.setActivityDate(LocalDate.now());
        selfCare.setActivityTitle(title);
        selfCare.setDurationMinutes(duration);
        selfCare.setNotes(notes);
        selfCare.setCreatedAt(LocalDateTime.now());
        selfCare.setUpdatedAt(LocalDateTime.now());

        selfCareRepository.save(selfCare);
        redirectAttributes.addFlashAttribute("success", "Music session saved!");
        
        return "redirect:/self-care";
    }
}

