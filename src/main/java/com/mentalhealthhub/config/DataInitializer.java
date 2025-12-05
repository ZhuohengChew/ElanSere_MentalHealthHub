package com.mentalhealthhub.config;

import com.mentalhealthhub.model.*;
import com.mentalhealthhub.repository.*;
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
        module1.setDescription("Learn the basics of mental health, common conditions, and how to recognize signs in yourself and others.");
        module1.setDurationMinutes(15);
        module1.setCategory("Foundation");
        module1.setImageUrl("https://images.unsplash.com/photo-1620147512372-9e00421556bb?w=400");
        module1.setContent("This module covers fundamental concepts of mental health, including common mental health conditions, warning signs, and when to seek help.");
        module1.setActive(true);
        module1.setCreatedAt(LocalDateTime.now());
        module1.setUpdatedAt(LocalDateTime.now());
        moduleRepository.save(module1);

        EducationalModule module2 = new EducationalModule();
        module2.setTitle("Managing Stress and Anxiety");
        module2.setDescription("Discover practical techniques to manage daily stress and anxiety, including cognitive behavioral strategies.");
        module2.setDurationMinutes(20);
        module2.setCategory("Coping Skills");
        module2.setImageUrl("https://images.unsplash.com/photo-1563417994954-2736db3bf2c9?w=400");
        module2.setContent("Learn effective strategies for managing stress and anxiety through cognitive behavioral techniques, breathing exercises, and mindfulness practices.");
        module2.setActive(true);
        module2.setCreatedAt(LocalDateTime.now());
        module2.setUpdatedAt(LocalDateTime.now());
        moduleRepository.save(module2);

        EducationalModule module3 = new EducationalModule();
        module3.setTitle("Building Resilience");
        module3.setDescription("Learn how to develop mental toughness and bounce back from challenges and setbacks.");
        module3.setDurationMinutes(18);
        module3.setCategory("Personal Growth");
        module3.setImageUrl("https://images.unsplash.com/photo-1740645581682-bc1e8e37b0f3?w=400");
        module3.setContent("Develop resilience through understanding your strengths, building support networks, and learning from adversity.");
        module3.setActive(true);
        module3.setCreatedAt(LocalDateTime.now());
        module3.setUpdatedAt(LocalDateTime.now());
        moduleRepository.save(module3);

        EducationalModule module4 = new EducationalModule();
        module4.setTitle("Sleep and Mental Health");
        module4.setDescription("Understand the connection between sleep quality and mental wellbeing, with tips for better sleep hygiene.");
        module4.setDurationMinutes(12);
        module4.setCategory("Wellness");
        module4.setImageUrl("https://images.unsplash.com/photo-1606733572375-35620adc4a18?w=400");
        module4.setContent("Explore the bidirectional relationship between sleep and mental health, and learn practical sleep hygiene strategies.");
        module4.setActive(true);
        module4.setCreatedAt(LocalDateTime.now());
        module4.setUpdatedAt(LocalDateTime.now());
        moduleRepository.save(module4);

        EducationalModule module5 = new EducationalModule();
        module5.setTitle("Recognizing Depression");
        module5.setDescription("Learn to identify symptoms of depression and understand when to seek professional help.");
        module5.setDurationMinutes(16);
        module5.setCategory("Foundation");
        module5.setImageUrl("https://images.unsplash.com/photo-1579017308347-e53e0d2fc5e9?w=400");
        module5.setContent("Understand the signs and symptoms of depression, when to seek help, and available treatment options.");
        module5.setActive(true);
        module5.setCreatedAt(LocalDateTime.now());
        module5.setUpdatedAt(LocalDateTime.now());
        moduleRepository.save(module5);

        EducationalModule module6 = new EducationalModule();
        module6.setTitle("Mindfulness Practices");
        module6.setDescription("Introduction to mindfulness meditation and its benefits for mental health and stress reduction.");
        module6.setDurationMinutes(14);
        module6.setCategory("Coping Skills");
        module6.setImageUrl("https://images.unsplash.com/photo-1758599879728-d7079db6505b?w=400");
        module6.setContent("Learn mindfulness meditation techniques and how to incorporate them into your daily routine for better mental health.");
        module6.setActive(true);
        module6.setCreatedAt(LocalDateTime.now());
        module6.setUpdatedAt(LocalDateTime.now());
        moduleRepository.save(module6);

        EducationalModule module7 = new EducationalModule();
        module7.setTitle("Healthy Relationships");
        module7.setDescription("Understanding how relationships impact mental health and how to build supportive connections.");
        module7.setDurationMinutes(17);
        module7.setCategory("Personal Growth");
        module7.setImageUrl("https://images.unsplash.com/photo-1713428856080-43fc975d2496?w=400");
        module7.setContent("Explore the importance of healthy relationships for mental wellbeing and learn how to build and maintain them.");
        module7.setActive(true);
        module7.setCreatedAt(LocalDateTime.now());
        module7.setUpdatedAt(LocalDateTime.now());
        moduleRepository.save(module7);

        EducationalModule module8 = new EducationalModule();
        module8.setTitle("Academic Stress Management");
        module8.setDescription("Strategies specific to managing stress related to academic pressures and deadlines.");
        module8.setDurationMinutes(19);
        module8.setCategory("Coping Skills");
        module8.setImageUrl("https://images.unsplash.com/photo-1620147512372-9e00421556bb?w=400");
        module8.setContent("Learn practical strategies for managing academic stress, including time management, study techniques, and self-care.");
        module8.setActive(true);
        module8.setCreatedAt(LocalDateTime.now());
        module8.setUpdatedAt(LocalDateTime.now());
        moduleRepository.save(module8);
    }

    private void initializeForumPosts() {
        // Get a student user or create a default one for posts
        User student = userRepository.findByEmail("student@example.com")
            .orElse(null);
        
        if (student == null) {
            // Only create if no users exist to avoid conflicts
            if (userRepository.count() == 0) {
                student = new User();
                student.setEmail("student@example.com");
                student.setName("Anonymous Student");
                student.setPassword("password"); // In production, this should be hashed
                student.setRole(UserRole.STUDENT);
                student = userRepository.save(student);
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

        ForumPost post1 = ForumPost.builder()
            .user(student)
            .title("Managing exam stress")
            .content("I have three exams coming up next week and I'm feeling really overwhelmed. Does anyone have tips on how to manage study stress?")
            .category("Academic Stress")
            .createdAt(LocalDateTime.now().minusHours(2))
            .updatedAt(LocalDateTime.now().minusHours(2))
            .views(12)
            .replies(8)
            .build();
        forumPostRepository.save(post1);

        ForumPost post2 = ForumPost.builder()
            .user(student)
            .title("Anxiety about social situations")
            .content("I find it really hard to attend group events or parties. Any advice on dealing with social anxiety?")
            .category("Anxiety")
            .createdAt(LocalDateTime.now().minusHours(5))
            .updatedAt(LocalDateTime.now().minusHours(5))
            .views(18)
            .replies(15)
            .build();
        forumPostRepository.save(post2);

        ForumPost post3 = ForumPost.builder()
            .user(student)
            .title("Staying motivated")
            .content("Lately I've been struggling to stay motivated with my coursework. How do you all keep going when things feel tough?")
            .category("Motivation")
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .views(24)
            .replies(19)
            .build();
        forumPostRepository.save(post3);

        ForumPost post4 = ForumPost.builder()
            .user(student)
            .title("Sleep schedule advice")
            .content("My sleep schedule is completely messed up and it's affecting my mental health. What strategies have worked for you?")
            .category("Self-Care")
            .createdAt(LocalDateTime.now().minusDays(2))
            .updatedAt(LocalDateTime.now().minusDays(2))
            .views(15)
            .replies(11)
            .build();
        forumPostRepository.save(post4);

        // Add some comments to the first post
        ForumComment comment1 = new ForumComment();
        comment1.setPost(post1);
        comment1.setUser(student);
        comment1.setContent("I totally understand how you feel. What helped me was breaking down my study time into smaller chunks and taking regular breaks.");
        comment1.setCreatedAt(LocalDateTime.now().minusHours(1));
        comment1.setUpdatedAt(LocalDateTime.now().minusHours(1));
        commentRepository.save(comment1);

        ForumComment comment2 = new ForumComment();
        comment2.setPost(post1);
        comment2.setUser(student);
        comment2.setContent("Try the Pomodoro technique - 25 minutes of focused study followed by a 5 minute break. It really works!");
        comment2.setCreatedAt(LocalDateTime.now().minusMinutes(45));
        comment2.setUpdatedAt(LocalDateTime.now().minusMinutes(45));
        commentRepository.save(comment2);
    }
}

