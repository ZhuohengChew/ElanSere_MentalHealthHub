package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.EducationalModule;
import com.mentalhealthhub.model.ModuleProgress;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.EducationalModuleRepository;
import com.mentalhealthhub.repository.ModuleProgressRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/modules")
public class ModuleController {

    @Autowired
    private EducationalModuleRepository moduleRepository;

    @Autowired
    private ModuleProgressRepository progressRepository;

    @GetMapping
    public String listModules(HttpSession session, Model model,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) String category) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<EducationalModule> modules;
        if (search != null && !search.isEmpty()) {
            modules = moduleRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);
        } else if (category != null && !category.isEmpty()) {
            modules = moduleRepository.findByCategoryAndActiveTrue(category);
        } else {
            modules = moduleRepository.findByActiveTrueOrderByCreatedAtDesc();
        }

        // Get user's progress for each module
        List<ModuleProgress> userProgress = progressRepository.findByUser(user);
        
        // Create a map of module IDs to progress for easy lookup in template
        java.util.Map<Long, ModuleProgress> progressMap = new java.util.HashMap<>();
        for (ModuleProgress progress : userProgress) {
            progressMap.put(progress.getModule().getId(), progress);
        }
        
        // Calculate overall progress
        long completedCount = progressRepository.countByUserAndCompletedTrue(user);
        double progressPercentage = modules.isEmpty() ? 0 : (completedCount * 100.0 / modules.size());

        model.addAttribute("user", user);
        model.addAttribute("modules", modules);
        model.addAttribute("userProgress", userProgress);
        model.addAttribute("progressMap", progressMap);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("totalModules", modules.size());
        model.addAttribute("progressPercentage", progressPercentage);
        model.addAttribute("search", search);
        model.addAttribute("category", category);
        model.addAttribute("page", "student/modules");
        model.addAttribute("title", "Educational Modules");
        model.addAttribute("activePage", "modules");

        return "layout";
    }

    @GetMapping("/{id}")
    public String viewModule(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        EducationalModule module = moduleRepository.findById(id).orElse(null);
        if (module == null || !module.getActive()) {
            return "redirect:/modules";
        }

        // Get user's progress for this module
        Optional<ModuleProgress> progressOpt = progressRepository.findByUserAndModule(user, module);
        ModuleProgress progress = progressOpt.orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("module", module);
        model.addAttribute("progress", progress);
        model.addAttribute("page", "student/module-detail");
        model.addAttribute("title", module.getTitle());
        model.addAttribute("activePage", "modules");

        return "layout";
    }

    @PostMapping("/{id}/start")
    public String startModule(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        EducationalModule module = moduleRepository.findById(id).orElse(null);
        if (module == null || !module.getActive()) {
            return "redirect:/modules";
        }

        Optional<ModuleProgress> progressOpt = progressRepository.findByUserAndModule(user, module);
        ModuleProgress progress;
        
        if (progressOpt.isPresent()) {
            progress = progressOpt.get();
            if (progress.getStartedAt() == null) {
                progress.setStartedAt(LocalDateTime.now());
            }
        } else {
            progress = new ModuleProgress();
            progress.setUser(user);
            progress.setModule(module);
            progress.setStartedAt(LocalDateTime.now());
            progress.setProgressPercentage(0);
            progress.setCompleted(false);
        }

        progressRepository.save(progress);
        redirectAttributes.addFlashAttribute("success", "Module started!");
        
        return "redirect:/modules/" + id;
    }

    @PostMapping("/{id}/complete")
    public String completeModule(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        EducationalModule module = moduleRepository.findById(id).orElse(null);
        if (module == null || !module.getActive()) {
            return "redirect:/modules";
        }

        Optional<ModuleProgress> progressOpt = progressRepository.findByUserAndModule(user, module);
        ModuleProgress progress;
        
        if (progressOpt.isPresent()) {
            progress = progressOpt.get();
        } else {
            progress = new ModuleProgress();
            progress.setUser(user);
            progress.setModule(module);
            progress.setStartedAt(LocalDateTime.now());
        }

        progress.setCompleted(true);
        progress.setProgressPercentage(100);
        progress.setCompletedAt(LocalDateTime.now());
        progressRepository.save(progress);
        
        redirectAttributes.addFlashAttribute("success", "Module completed! Great job!");
        
        return "redirect:/modules/" + id;
    }

    @PostMapping("/{id}/progress")
    public String updateProgress(@PathVariable Long id, 
                                @RequestParam Integer percentage,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        EducationalModule module = moduleRepository.findById(id).orElse(null);
        if (module == null || !module.getActive()) {
            return "redirect:/modules";
        }

        Optional<ModuleProgress> progressOpt = progressRepository.findByUserAndModule(user, module);
        ModuleProgress progress;
        
        if (progressOpt.isPresent()) {
            progress = progressOpt.get();
        } else {
            progress = new ModuleProgress();
            progress.setUser(user);
            progress.setModule(module);
            progress.setStartedAt(LocalDateTime.now());
        }

        progress.setProgressPercentage(Math.min(100, Math.max(0, percentage)));
        if (progress.getProgressPercentage() == 100) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
        }
        
        progressRepository.save(progress);
        
        return "redirect:/modules/" + id;
    }
}

