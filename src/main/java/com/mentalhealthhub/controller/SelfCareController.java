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
import java.util.List;

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
                user, SelfCareType.MOOD, weekAgo, today);

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
        // Note: activeTab flash attribute is automatically available in model after
        // redirect

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
    public String startMeditation(
            @RequestParam String title,
            @RequestParam(required = false) Integer duration,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Store meditation session data in session for the session page
        session.setAttribute("meditationTitle", title);
        session.setAttribute("meditationDuration", duration != null ? duration : 10);

        return "redirect:/self-care/meditation/session";
    }

    @GetMapping("/meditation/session")
    public String meditationSession(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        String title = (String) session.getAttribute("meditationTitle");
        Integer duration = (Integer) session.getAttribute("meditationDuration");

        if (title == null || duration == null) {
            return "redirect:/self-care";
        }

        model.addAttribute("user", user);
        model.addAttribute("meditationTitle", title);
        model.addAttribute("meditationDuration", duration);
        model.addAttribute("page", "student/meditation-session");
        model.addAttribute("title", "Meditation Session");
        model.addAttribute("activePage", "self-care");

        return "layout";
    }

    @PostMapping("/meditation/complete")
    public String completeMeditation(
            @RequestParam String title,
            @RequestParam Integer duration,
            @RequestParam(required = false) Integer completedDuration,
            @RequestParam(required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Clear session attributes
        session.removeAttribute("meditationTitle");
        session.removeAttribute("meditationDuration");

        SelfCare selfCare = new SelfCare();
        selfCare.setUser(user);
        selfCare.setType(SelfCareType.MEDITATION);
        selfCare.setActivityDate(LocalDate.now());
        selfCare.setActivityTitle(title);
        selfCare.setDurationMinutes(completedDuration != null ? completedDuration : duration);
        selfCare.setNotes(notes);
        selfCare.setCreatedAt(LocalDateTime.now());
        selfCare.setUpdatedAt(LocalDateTime.now());

        selfCareRepository.save(selfCare);
        redirectAttributes.addFlashAttribute("success",
                "Meditation session completed! Great job taking time for yourself.");

        return "redirect:/self-care";
    }

    @PostMapping("/breathing")
    public String startBreathing(
            @RequestParam String technique,
            @RequestParam(required = false) Integer breathDuration,
            @RequestParam(required = false) Integer duration,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Store breathing session data in session for the session page
        session.setAttribute("breathingTechnique", technique);
        session.setAttribute("breathingBreathDuration", breathDuration != null ? breathDuration : 4);
        session.setAttribute("breathingDuration", duration != null ? duration : 5);

        return "redirect:/self-care/breathing/session";
    }

    @GetMapping("/breathing/session")
    public String breathingSession(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        String technique = (String) session.getAttribute("breathingTechnique");
        Integer breathDuration = (Integer) session.getAttribute("breathingBreathDuration");
        Integer duration = (Integer) session.getAttribute("breathingDuration");

        if (technique == null) {
            return "redirect:/self-care";
        }

        model.addAttribute("user", user);
        model.addAttribute("breathingTechnique", technique);
        model.addAttribute("breathingBreathDuration", breathDuration != null ? breathDuration : 4);
        model.addAttribute("breathingDuration", duration != null ? duration : 5);
        model.addAttribute("page", "student/breathing-session");
        model.addAttribute("title", "Breathing Exercise");
        model.addAttribute("activePage", "self-care");

        return "layout";
    }

    @PostMapping("/breathing/complete")
    public String completeBreathing(
            @RequestParam String technique,
            @RequestParam(required = false) Integer breathDuration,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) Integer completedDuration,
            @RequestParam(required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Clear session attributes
        session.removeAttribute("breathingTechnique");
        session.removeAttribute("breathingBreathDuration");
        session.removeAttribute("breathingDuration");

        SelfCare selfCare = new SelfCare();
        selfCare.setUser(user);
        selfCare.setType(SelfCareType.BREATHING);
        selfCare.setActivityDate(LocalDate.now());
        selfCare.setBreathingTechnique(technique);
        selfCare.setBreathDuration(breathDuration);
        selfCare.setDurationMinutes(completedDuration != null ? completedDuration : duration);
        selfCare.setNotes(notes);
        selfCare.setCreatedAt(LocalDateTime.now());
        selfCare.setUpdatedAt(LocalDateTime.now());

        selfCareRepository.save(selfCare);
        redirectAttributes.addFlashAttribute("success",
                "Breathing exercise completed! Great job taking time for yourself.");

        return "redirect:/self-care";
    }

    @PostMapping("/exercise")
    public String startExercise(
            @RequestParam String title,
            @RequestParam(required = false) Integer duration,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Store exercise session data in session for the session page
        session.setAttribute("exerciseTitle", title);
        session.setAttribute("exerciseDuration", duration != null ? duration : 10);

        return "redirect:/self-care/exercise/session";
    }

    @GetMapping("/exercise/session")
    public String exerciseSession(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        String title = (String) session.getAttribute("exerciseTitle");
        Integer duration = (Integer) session.getAttribute("exerciseDuration");

        if (title == null || duration == null) {
            return "redirect:/self-care";
        }

        model.addAttribute("user", user);
        model.addAttribute("exerciseTitle", title);
        model.addAttribute("exerciseDuration", duration);
        model.addAttribute("page", "student/exercise-session");
        model.addAttribute("title", "Exercise Session");
        model.addAttribute("activePage", "self-care");

        return "layout";
    }

    @PostMapping("/exercise/complete")
    public String completeExercise(
            @RequestParam String title,
            @RequestParam Integer duration,
            @RequestParam(required = false) Integer completedDuration,
            @RequestParam(required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Clear session attributes
        session.removeAttribute("exerciseTitle");
        session.removeAttribute("exerciseDuration");

        SelfCare selfCare = new SelfCare();
        selfCare.setUser(user);
        selfCare.setType(SelfCareType.EXERCISE);
        selfCare.setActivityDate(LocalDate.now());
        selfCare.setActivityTitle(title);
        selfCare.setDurationMinutes(completedDuration != null ? completedDuration : duration);
        selfCare.setNotes(notes);
        selfCare.setCreatedAt(LocalDateTime.now());
        selfCare.setUpdatedAt(LocalDateTime.now());

        selfCareRepository.save(selfCare);
        redirectAttributes.addFlashAttribute("success",
                "Exercise session completed! Great job taking time for yourself.");

        return "redirect:/self-care";
    }

    @PostMapping("/music")
    public String startMusic(
            @RequestParam String title,
            @RequestParam(required = false) Integer duration,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Store music session data in session for the session page
        session.setAttribute("musicTitle", title);
        session.setAttribute("musicDuration", duration != null ? duration : 30);

        return "redirect:/self-care/music/session";
    }

    @GetMapping("/music/session")
    public String musicSession(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        String title = (String) session.getAttribute("musicTitle");
        Integer duration = (Integer) session.getAttribute("musicDuration");

        if (title == null || duration == null) {
            return "redirect:/self-care";
        }

        model.addAttribute("user", user);
        model.addAttribute("musicTitle", title);
        model.addAttribute("musicDuration", duration);
        model.addAttribute("page", "student/music-session");
        model.addAttribute("title", "Music Session");
        model.addAttribute("activePage", "self-care");

        return "layout";
    }

    @PostMapping("/music/complete")
    public String completeMusic(
            @RequestParam String title,
            @RequestParam Integer duration,
            @RequestParam(required = false) Integer completedDuration,
            @RequestParam(required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Clear session attributes
        session.removeAttribute("musicTitle");
        session.removeAttribute("musicDuration");

        SelfCare selfCare = new SelfCare();
        selfCare.setUser(user);
        selfCare.setType(SelfCareType.MUSIC);
        selfCare.setActivityDate(LocalDate.now());
        selfCare.setActivityTitle(title);
        selfCare.setDurationMinutes(completedDuration != null ? completedDuration : duration);
        selfCare.setNotes(notes);
        selfCare.setCreatedAt(LocalDateTime.now());
        selfCare.setUpdatedAt(LocalDateTime.now());

        selfCareRepository.save(selfCare);
        redirectAttributes.addFlashAttribute("success", "Music session completed! Great job taking time for yourself.");

        return "redirect:/self-care";
    }
}
