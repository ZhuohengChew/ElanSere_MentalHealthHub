package com.mentalhealthhub.config;

import com.mentalhealthhub.model.*;
import com.mentalhealthhub.repository.*;
import com.mentalhealthhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

        @Autowired
        private EducationalModuleRepository moduleRepository;

        @Autowired
        private ForumPostRepository forumPostRepository;

        @Autowired
        private ForumCommentRepository commentRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private UserService userService;

        @Override
        public void run(String... args) {
                // Only initialize if database is empty
                if (moduleRepository.count() == 0) {
                        initializeEducationalModules();
                }

                if (forumPostRepository.count() == 0) {
                        initializeForumPosts();
                }
        }

        private void initializeEducationalModules() {
                EducationalModule module1 = new EducationalModule();
                module1.setTitle("Understanding Mental Health");
                module1.setDescription(
                                "Learn the basics of mental health, common conditions, and how to recognize signs in yourself and others.");
                module1.setDurationMinutes(15);
                module1.setCategory("Foundation");
                module1.setImageUrl("https://images.unsplash.com/photo-1620147512372-9e00421556bb?w=400");
                module1.setContent(
                                "This module covers fundamental concepts of mental health, including common mental health conditions, warning signs, and when to seek help.");
                module1.setActive(true);
                module1.setCreatedAt(LocalDateTime.now());
                module1.setUpdatedAt(LocalDateTime.now());
                moduleRepository.save(module1);

                EducationalModule module2 = new EducationalModule();
                module2.setTitle("Managing Stress and Anxiety");
                module2.setDescription(
                                "Discover practical techniques to manage daily stress and anxiety, including cognitive behavioral strategies.");
                module2.setDurationMinutes(20);
                module2.setCategory("Coping Skills");
                module2.setImageUrl("https://images.unsplash.com/photo-1563417994954-2736db3bf2c9?w=400");
                module2.setContent(
                                "Learn effective strategies for managing stress and anxiety through cognitive behavioral techniques, breathing exercises, and mindfulness practices.");
                module2.setActive(true);
                module2.setCreatedAt(LocalDateTime.now());
                module2.setUpdatedAt(LocalDateTime.now());
                moduleRepository.save(module2);

                EducationalModule module3 = new EducationalModule();
                module3.setTitle("Building Resilience");
                module3.setDescription(
                                "Learn how to develop mental toughness and bounce back from challenges and setbacks.");
                module3.setDurationMinutes(18);
                module3.setCategory("Personal Growth");
                module3.setImageUrl("https://images.unsplash.com/photo-1740645581682-bc1e8e37b0f3?w=400");
                module3.setContent(
                                "Develop resilience through understanding your strengths, building support networks, and learning from adversity.");
                module3.setActive(true);
                module3.setCreatedAt(LocalDateTime.now());
                module3.setUpdatedAt(LocalDateTime.now());
                moduleRepository.save(module3);

                EducationalModule module4 = new EducationalModule();
                module4.setTitle("Sleep and Mental Health");
                module4.setDescription(
                                "Understand the connection between sleep quality and mental wellbeing, with tips for better sleep hygiene.");
                module4.setDurationMinutes(12);
                module4.setCategory("Wellness");
                module4.setImageUrl("https://images.unsplash.com/photo-1606733572375-35620adc4a18?w=400");
                module4.setContent(
                                "Explore the bidirectional relationship between sleep and mental health, and learn practical sleep hygiene strategies.");
                module4.setActive(true);
                module4.setCreatedAt(LocalDateTime.now());
                module4.setUpdatedAt(LocalDateTime.now());
                moduleRepository.save(module4);

                EducationalModule module5 = new EducationalModule();
                module5.setTitle("Recognizing Depression");
                module5.setDescription(
                                "Learn to identify symptoms of depression and understand when to seek professional help.");
                module5.setDurationMinutes(16);
                module5.setCategory("Foundation");
                module5.setImageUrl("https://images.unsplash.com/photo-1579017308347-e53e0d2fc5e9?w=400");
                module5.setContent(
                                "Understand the signs and symptoms of depression, when to seek help, and available treatment options.");
                module5.setActive(true);
                module5.setCreatedAt(LocalDateTime.now());
                module5.setUpdatedAt(LocalDateTime.now());
                moduleRepository.save(module5);

                EducationalModule module6 = new EducationalModule();
                module6.setTitle("Mindfulness Practices");
                module6.setDescription(
                                "Introduction to mindfulness meditation and its benefits for mental health and stress reduction.");
                module6.setDurationMinutes(14);
                module6.setCategory("Coping Skills");
                module6.setImageUrl("https://images.unsplash.com/photo-1758599879728-d7079db6505b?w=400");
                module6.setContent(
                                "Learn mindfulness meditation techniques and how to incorporate them into your daily routine for better mental health.");
                module6.setActive(true);
                module6.setCreatedAt(LocalDateTime.now());
                module6.setUpdatedAt(LocalDateTime.now());
                moduleRepository.save(module6);

                EducationalModule module7 = new EducationalModule();
                module7.setTitle("Healthy Relationships");
                module7.setDescription(
                                "Understanding how relationships impact mental health and how to build supportive connections.");
                module7.setDurationMinutes(17);
                module7.setCategory("Personal Growth");
                module7.setImageUrl("https://images.unsplash.com/photo-1713428856080-43fc975d2496?w=400");
                module7.setContent(
                                "Explore the importance of healthy relationships for mental wellbeing and learn how to build and maintain them.");
                module7.setActive(true);
                module7.setCreatedAt(LocalDateTime.now());
                module7.setUpdatedAt(LocalDateTime.now());
                moduleRepository.save(module7);

                EducationalModule module8 = new EducationalModule();
                module8.setTitle("Academic Stress Management");
                module8.setDescription(
                                "Strategies specific to managing stress related to academic pressures and deadlines.");
                module8.setDurationMinutes(19);
                module8.setCategory("Coping Skills");
                module8.setImageUrl("https://images.unsplash.com/photo-1620147512372-9e00421556bb?w=400");
                module8.setContent(
                                "Learn practical strategies for managing academic stress, including time management, study techniques, and self-care.");
                module8.setActive(true);
                module8.setCreatedAt(LocalDateTime.now());
                module8.setUpdatedAt(LocalDateTime.now());
                moduleRepository.save(module8);

                EducationalModule module9 = new EducationalModule();
                module9.setTitle("Digital Wellbeing & Social Media");
                module9.setDescription(
                                "Understand how social media and screen time impact your mental health, and learn strategies for healthy digital habits.");
                module9.setDurationMinutes(20);
                module9.setCategory("Wellness");
                module9.setImageUrl("https://images.unsplash.com/photo-1517244864778-5ee2fda3db5e?w=400");
                module9.setContent(
                                "This module explores the relationship between digital technology and mental wellbeing, including practical steps for managing notifications, curating your feeds, and setting boundaries with devices.");
                module9.setActive(true);
                module9.setCreatedAt(LocalDateTime.now());
                module9.setUpdatedAt(LocalDateTime.now());
                moduleRepository.save(module9);

                EducationalModule module10 = new EducationalModule();
                module10.setTitle("Self‑Compassion and Positive Self‑Talk");
                module10.setDescription(
                                "Learn how to treat yourself with kindness, challenge negative self‑talk, and build healthier inner dialogue.");
                module10.setDurationMinutes(18);
                module10.setCategory("Personal Growth");
                module10.setImageUrl("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400");
                module10.setContent(
                                "In this module, you will practice self‑compassion exercises, learn how to reframe harsh self‑criticism, and create personal affirmations that support your resilience.");
                module10.setActive(true);
                module10.setCreatedAt(LocalDateTime.now());
                module10.setUpdatedAt(LocalDateTime.now());
                moduleRepository.save(module10);
        }

        private void initializeForumPosts() {
                // Get a student user or create a default one for posts
                User student = userRepository.findByEmail("student@example.com")
                                .orElse(null);

                if (student == null) {
                        // Only create if no users exist to avoid conflicts
                        if (userRepository.count() == 0) {
                                try {
                                        student = userService.registerUser("student@example.com", "password",
                                                        "Anonymous Student", UserRole.STUDENT);
                                } catch (Exception e) {
                                        // User might already exist, try to fetch it
                                        student = userRepository.findByEmail("student@example.com").orElse(null);
                                }
                        } else {
                                // Use first student found
                                student = userRepository.findAll().stream()
                                                .filter(u -> u.getRole() == UserRole.STUDENT)
                                                .findFirst()
                                                .orElse(null);
                        }
                }

                if (student == null) {
                        return; // Skip if no student user available
                }

                // Forum posts will be created by users
        }
}
