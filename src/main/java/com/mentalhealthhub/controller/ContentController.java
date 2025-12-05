package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.Content;
import com.mentalhealthhub.model.ContentStatus;
import com.mentalhealthhub.model.ContentType;
import com.mentalhealthhub.model.EducationalModule;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import com.mentalhealthhub.repository.ContentRepository;
import com.mentalhealthhub.repository.EducationalModuleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/content")
public class ContentController {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private EducationalModuleRepository moduleRepository;

    @GetMapping
    public String listContent(HttpSession session, Model model,
                             @RequestParam(required = false) String search,
                             @RequestParam(required = false) String filterType,
                             @RequestParam(required = false) String filterStatus) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/login";
        }

        List<Content> allContent = contentRepository.findAll();
        
        // Get all EducationalModules and convert them to Content-like objects for display
        List<EducationalModule> allModules = moduleRepository.findAll();
        
        // Apply filters to Content
        List<Content> filteredContent = allContent.stream()
            .filter(content -> {
                boolean matchesSearch = search == null || search.isEmpty() ||
                    content.getTitle().toLowerCase().contains(search.toLowerCase()) ||
                    content.getDescription().toLowerCase().contains(search.toLowerCase());
                
                boolean matchesType = filterType == null || filterType.equals("all") ||
                    content.getType().name().equalsIgnoreCase(filterType);
                
                boolean matchesStatus = filterStatus == null || filterStatus.equals("all") ||
                    content.getStatus().name().equalsIgnoreCase(filterStatus);
                
                return matchesSearch && matchesType && matchesStatus;
            })
            .collect(Collectors.toList());

        // Filter modules based on search
        List<EducationalModule> filteredModules = allModules.stream()
            .filter(module -> {
                if (search == null || search.isEmpty()) {
                    return true;
                }
                return module.getTitle().toLowerCase().contains(search.toLowerCase()) ||
                       module.getDescription().toLowerCase().contains(search.toLowerCase());
            })
            .collect(Collectors.toList());

        // Calculate statistics including modules
        long totalContent = allContent.size() + allModules.size();
        long publishedCount = allContent.stream()
            .filter(c -> c.getStatus() == ContentStatus.PUBLISHED)
            .count() + allModules.stream().filter(m -> m.getActive()).count();
        long draftCount = allContent.stream()
            .filter(c -> c.getStatus() == ContentStatus.DRAFT)
            .count() + allModules.stream().filter(m -> !m.getActive()).count();
        Set<String> categories = new java.util.HashSet<>();
        allContent.forEach(c -> categories.add(c.getCategory()));
        allModules.forEach(m -> categories.add(m.getCategory()));

        model.addAttribute("user", user);
        model.addAttribute("content", filteredContent);
        model.addAttribute("modules", filteredModules);
        model.addAttribute("totalContent", totalContent);
        model.addAttribute("publishedCount", publishedCount);
        model.addAttribute("draftCount", draftCount);
        model.addAttribute("categoryCount", categories.size());
        model.addAttribute("search", search);
        model.addAttribute("filterType", filterType);
        model.addAttribute("filterStatus", filterStatus);
        model.addAttribute("page", "admin/manage-content");
        model.addAttribute("title", "Content Management");
        model.addAttribute("activePage", "content-management");

        return "layout";
    }

    @PostMapping("/save")
    public String saveContent(@RequestParam(required = false) Long id,
                             @RequestParam String title,
                             @RequestParam String type,
                             @RequestParam String category,
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

        Content content;
        if (id != null) {
            content = contentRepository.findById(id).orElse(new Content());
        } else {
            content = new Content();
            content.setCreatedAt(LocalDateTime.now());
        }

        content.setTitle(title);
        content.setType(ContentType.valueOf(type.toUpperCase()));
        content.setCategory(category);
        content.setDescription(description);
        content.setContent(contentBody);
        content.setUrl(url);
        content.setStatus(ContentStatus.valueOf(status.toUpperCase()));
        content.setUpdatedAt(LocalDateTime.now());

        contentRepository.save(content);

        // If content type is MODULE, also create/update EducationalModule
        if (content.getType() == ContentType.MODULE) {
            EducationalModule module;
            java.util.Optional<EducationalModule> existingModule = moduleRepository.findByTitle(content.getTitle());
            if (existingModule.isPresent()) {
                module = existingModule.get();
            } else {
                module = new EducationalModule();
            }

            module.setTitle(content.getTitle());
            module.setDescription(content.getDescription());
            module.setCategory(content.getCategory());
            module.setContent(content.getContent());
            module.setImageUrl(content.getUrl()); // Use URL as image URL
            module.setDurationMinutes(15); // Default duration, can be enhanced
            module.setActive(content.getStatus() == ContentStatus.PUBLISHED);
            
            if (module.getCreatedAt() == null) {
                module.setCreatedAt(LocalDateTime.now());
            }
            module.setUpdatedAt(LocalDateTime.now());

            moduleRepository.save(module);
        }

        redirectAttributes.addFlashAttribute("success", id != null ? "Content updated successfully!" : "Content added successfully!");
        
        return "redirect:/admin/content";
    }

    @PostMapping("/delete/{id}")
    public String deleteContent(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/login";
        }

        // Check if content is a module before deleting
        Content content = contentRepository.findById(id).orElse(null);
        if (content != null && content.getType() == ContentType.MODULE) {
            // Deactivate or delete corresponding EducationalModule
            java.util.Optional<EducationalModule> module = moduleRepository.findByTitle(content.getTitle());
            if (module.isPresent()) {
                EducationalModule eduModule = module.get();
                eduModule.setActive(false); // Deactivate instead of delete to preserve data
                moduleRepository.save(eduModule);
            }
        }

        contentRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Content deleted successfully!");
        
        return "redirect:/admin/content";
    }
}

