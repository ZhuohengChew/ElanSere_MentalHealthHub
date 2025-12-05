package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.ForumComment;
import com.mentalhealthhub.model.ForumPost;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.ForumCommentRepository;
import com.mentalhealthhub.repository.ForumPostRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/forum")
public class ForumController {

    @Autowired
    private ForumPostRepository forumPostRepository;

    @Autowired
    private ForumCommentRepository commentRepository;

    @GetMapping
    public String listPosts(Model model, HttpSession session,
                           @RequestParam(required = false) String category) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Optimized: Get posts efficiently
        List<ForumPost> posts;
        if (category != null && !category.isEmpty() && !category.equals("All Topics")) {
            posts = forumPostRepository.findByCategoryOrderByCreatedAtDesc(category);
        } else {
            posts = forumPostRepository.findAllByOrderByCreatedAtDesc();
        }
        
        // Limit to prevent lag (only show recent 50 posts)
        if (posts.size() > 50) {
            posts = posts.subList(0, 50);
        }

        // Calculate statistics efficiently - use count queries
        long totalPosts = forumPostRepository.count();
        long userPosts = forumPostRepository.countByUser(user);
        long totalInteractions = forumPostRepository.findAll().stream()
            .mapToLong(p -> (long) p.getReplies())
            .sum();

        model.addAttribute("posts", posts);
        model.addAttribute("user", user);
        model.addAttribute("category", category);
        model.addAttribute("totalPosts", totalPosts);
        model.addAttribute("userPosts", userPosts);
        model.addAttribute("totalInteractions", totalInteractions);
        model.addAttribute("page", "forum/list");
        model.addAttribute("title", "Peer Support Forum");
        model.addAttribute("activePage", "forum");

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

        // Get comments
        List<ForumComment> comments = commentRepository.findByPostOrderByCreatedAtAsc(post);
        
        // Update replies count
        post.setReplies(comments.size());
        forumPostRepository.save(post);

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("user", user);
        model.addAttribute("page", "forum/view");
        model.addAttribute("title", post.getTitle());
        model.addAttribute("activePage", "forum");

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
        model.addAttribute("title", "Create New Post");
        model.addAttribute("activePage", "forum");

        return "layout";
    }

    @PostMapping("/save")
    public String savePost(@RequestParam String title,
                           @RequestParam String content,
                           @RequestParam(required = false) String category,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        ForumPost post = ForumPost.builder()
            .user(user)
            .title(title)
            .content(content)
            .category(category != null && !category.isEmpty() ? category : "General")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .views(0)
            .replies(0)
            .build();

        forumPostRepository.save(post);
        redirectAttributes.addFlashAttribute("success", "Your post has been published successfully! 🎉");
        
        return "redirect:/forum";
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id,
                            @RequestParam String content,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        ForumPost post = forumPostRepository.findById(id).orElse(null);
        if (post == null) {
            return "redirect:/forum";
        }

        ForumComment comment = new ForumComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        commentRepository.save(comment);

        // Update replies count
        post.setReplies(commentRepository.countByPost(post).intValue());
        forumPostRepository.save(post);

        redirectAttributes.addFlashAttribute("success", "Comment posted successfully!");
        
        return "redirect:/forum/" + id;
    }
}
