package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.ContentType;
import com.mentalhealthhub.model.EducationalModule;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import com.mentalhealthhub.repository.EducationalModuleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/content")
public class ContentController {

    private final EducationalModuleRepository moduleRepository;
    private final com.mentalhealthhub.repository.ModuleProgressRepository progressRepository;

    public ContentController(
            EducationalModuleRepository moduleRepository,
            com.mentalhealthhub.repository.ModuleProgressRepository progressRepository) {
        this.moduleRepository = moduleRepository;
        this.progressRepository = progressRepository;
    }

    @GetMapping
    public String listContent(HttpSession session, Model model,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filterType,
            @RequestParam(required = false) String filterStatus) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/login";
        }

        // Fetch all modules (Unified Content)
        List<EducationalModule> allModules = moduleRepository.findAll();
        // Sort by CreatedAt DESC (Newest first)
        allModules.sort(
                Comparator.comparing(EducationalModule::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed());

        // Filter Logic
        List<EducationalModule> filteredModules = allModules.stream()
                .filter(m -> {
                    // Search
                    boolean matchesSearch = search == null || search.isEmpty() ||
                            m.getTitle().toLowerCase().contains(search.toLowerCase()) ||
                            (m.getDescription() != null
                                    && m.getDescription().toLowerCase().contains(search.toLowerCase()));

                    // Filter Type
                    boolean matchesType = true;
                    if (filterType != null && !filterType.equals("all")) {
                        // Handle null contentType by defaulting to MODULE or excluding
                        matchesType = m.getContentType() != null &&
                                m.getContentType().name().equalsIgnoreCase(filterType);
                    }

                    // Filter Status
                    boolean matchesStatus = true;
                    if (filterStatus != null && !filterStatus.equals("all")) {
                        boolean isActive = Boolean.TRUE.equals(m.getActive());
                        if (filterStatus.equalsIgnoreCase("active") || filterStatus.equalsIgnoreCase("published")) {
                            matchesStatus = isActive;
                        } else if (filterStatus.equalsIgnoreCase("inactive")
                                || filterStatus.equalsIgnoreCase("draft")) {
                            matchesStatus = !isActive;
                        }
                    }

                    return matchesSearch && matchesType && matchesStatus;
                })
                .collect(Collectors.toList());

        // Global Statistics (from Database, per requirement)
        long totalContent = allModules.size();
        long publishedCount = allModules.stream().filter(m -> Boolean.TRUE.equals(m.getActive())).count();
        long draftCount = totalContent - publishedCount;

        Set<String> categories = new java.util.HashSet<>();
        allModules.forEach(m -> {
            if (m.getCategory() != null)
                categories.add(m.getCategory());
        });

        model.addAttribute("user", user);
        model.addAttribute("modules", filteredModules); // Only display filtered
        model.addAttribute("totalContent", totalContent);
        model.addAttribute("publishedCount", publishedCount);
        model.addAttribute("draftCount", draftCount);
        model.addAttribute("categoryCount", categories.size());

        // Pass filters back to view for sticky inputs
        model.addAttribute("search", search);
        // Ensure we pass the exact value used in <option value="...">
        model.addAttribute("filterType", filterType != null ? filterType : "all");
        model.addAttribute("filterStatus", filterStatus != null ? filterStatus : "all");

        model.addAttribute("page", "admin/manage-content");
        model.addAttribute("title", "Content Management");
        model.addAttribute("activePage", "content-management");
        return "layout";
    }

    @GetMapping("/module/get/{id}")
    @ResponseBody
    public ResponseEntity<EducationalModule> getModule(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        return moduleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/module/save")
    public String saveModule(@RequestParam(required = false) Long id,
            @RequestParam String title,
            @RequestParam String type,
            @RequestParam String category,
            @RequestParam(required = false) Integer duration,
            @RequestParam String description,
            @RequestParam(required = false) String contentBody,
            @RequestParam(required = false) String url,
            @RequestParam String status,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/login";
        }

        EducationalModule module;
        if (id != null) {
            module = moduleRepository.findById(id).orElse(new EducationalModule());
        } else {
            module = new EducationalModule();
            module.setCreatedAt(LocalDateTime.now());
        }

        module.setTitle(title);
        module.setContentType(ContentType.valueOf(type.toUpperCase()));
        module.setCategory(category);
        module.setDurationMinutes(duration);
        module.setDescription(description);
        module.setContent(contentBody);
        module.setUrl(url); // Generic URL field
        module.setImageUrl(url); // Map URL to ImageURL as requested (Picture URL)

        // Status mapping: "published" or "active"
        module.setActive(status.equalsIgnoreCase("published") || status.equalsIgnoreCase("active"));
        module.setUpdatedAt(LocalDateTime.now());

        // Ensure non-null duration
        if (module.getDurationMinutes() == null) {
            module.setDurationMinutes(15);
        }

        moduleRepository.save(module);

        redirectAttributes.addFlashAttribute("success",
                (id != null ? "Content updated" : "Content created") + " successfully!");
        return "redirect:/admin/content";
    }

    @PostMapping("/module/delete/{id}")
    @org.springframework.transaction.annotation.Transactional
    public String deleteModule(@PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/login";
        }

        EducationalModule module = moduleRepository.findById(id).orElse(null);
        if (module != null) {
            // Delete associated progress first to avoid foreign key constraints
            progressRepository.deleteByModule(module);

            // Hard delete the module
            moduleRepository.delete(module);
            redirectAttributes.addFlashAttribute("success", "Item deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Item not found.");
        }

        return "redirect:/admin/content";
    }

    // Add ContentViewModel inner class for unified view
    public static class ContentViewModel {
        public Long id;
        public String title;
        public String type;
        public String category;
        public String description;
        public String content;
        public String url;
        public String status;
        public java.time.LocalDateTime updatedAt;
        public boolean isModule;

        public ContentViewModel(Long id, String title, String type, String category, String description, String content,
                String url, String status, java.time.LocalDateTime updatedAt, boolean isModule) {
            this.id = id;
            this.title = title;
            this.type = type;
            this.category = category;
            this.description = description;
            this.content = content;
            this.url = url;
            this.status = status;
            this.updatedAt = updatedAt;
            this.isModule = isModule;
        }
    }
}
