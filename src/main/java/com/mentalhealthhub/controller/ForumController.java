package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.ForumPost;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.ForumPostRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/forum")
public class ForumController {

    @Autowired
    private ForumPostRepository forumPostRepository;

    @GetMapping
    public String listPosts(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<ForumPost> posts = forumPostRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("posts", posts);
        model.addAttribute("user", user);
        
        model.addAttribute("page", "forum/list");
        model.addAttribute("title", "Forum List");

        return "layout";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        ForumPost post = forumPostRepository.findById(id).orElse(null);
        if (post == null) {
            return "redirect:/forum";
        }

        // Increment views
        post.setViews(post.getViews() + 1);
        forumPostRepository.save(post);

        model.addAttribute("post", post);
        model.addAttribute("user", user);

        model.addAttribute("page", "forum/view");
        model.addAttribute("title", "Forum View");
        
        return "layout";
    }

    @GetMapping("/new")
    public String newPost(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        model.addAttribute("page", "forum/new");
        model.addAttribute("title", "New Forum Post");
        return "layout";
    }

    @PostMapping("/save")
    public String savePost(@RequestParam String title,
                           @RequestParam String content,
                           HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        ForumPost post = ForumPost.builder()
            .user(user)
            .title(title)
            .content(content)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .views(0)
            .replies(0)
            .build();

        forumPostRepository.save(post);
        return "redirect:/forum";
    }
}
