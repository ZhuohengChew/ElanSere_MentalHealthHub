package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.EducationalModule;
import com.mentalhealthhub.model.ModuleProgress;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.EducationalModuleRepository;
import com.mentalhealthhub.repository.ModuleProgressRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;

@Controller
@RequestMapping("/modules")
public class ModuleController {

    private final EducationalModuleRepository moduleRepository;
    private final ModuleProgressRepository progressRepository;

    public ModuleController(EducationalModuleRepository moduleRepository, ModuleProgressRepository progressRepository) {
        this.moduleRepository = moduleRepository;
        this.progressRepository = progressRepository;
    }

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

        // Sort modules: Incomplete first, then Completed. Secondary sort: CreatedAt
        // DESC (Newest first)
        modules.sort(Comparator.comparing((EducationalModule m) -> {
            ModuleProgress p = progressMap.get(m.getId());
            return p != null && Boolean.TRUE.equals(p.getCompleted()); // Incomplete (false) first
        }).thenComparing(
                Comparator.comparing(EducationalModule::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed()));

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

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadModulePdf(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // If not logged in, just redirect to login page URL
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, "/login");
            return ResponseEntity.status(302).headers(headers).build();
        }

        EducationalModule module = moduleRepository.findById(id).orElse(null);
        if (module == null || !Boolean.TRUE.equals(module.getActive())) {
            return ResponseEntity.notFound().build();
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph(module.getTitle()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Category: " + module.getCategory()));
            document.add(new Paragraph("Estimated duration: " + module.getDurationMinutes() + " minutes"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Overview:"));
            document.add(new Paragraph(module.getDescription()));
            document.add(new Paragraph(" "));

            String rawContent = module.getContent() != null ? module.getContent() : "";
            // Very simple HTML tag stripping for PDF text
            String textContent = rawContent.replaceAll("<[^>]*>", "");
            if (!textContent.isBlank()) {
                document.add(new Paragraph("Module Content:"));
                document.add(new Paragraph(textContent));
            } else {
                document.add(new Paragraph("Module content will be available soon."));
            }

            document.close();

            byte[] pdfBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(
                    "attachment",
                    module.getTitle().replaceAll("[^a-zA-Z0-9\\-_]", "_") + ".pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
