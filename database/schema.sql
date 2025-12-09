-- =====================================================
-- Mental Health Hub - Database Schema
-- MySQL Database Setup Script
-- =====================================================
-- This script creates all necessary tables for the Mental Health Hub application
-- Run this in your MySQL database (via XAMPP PhpMyAdmin)

-- =====================================================
-- 1. USERS TABLE
-- =====================================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'STUDENT',
    -- Enum values: STUDENT, STAFF, PROFESSIONAL, ADMIN
    active BOOLEAN DEFAULT TRUE,
    profile_picture LONGTEXT,
    -- Mental health assessment scores
    stress_level INT,
    -- Range: 0-100
    anxiety_level INT,
    -- Range: 0-100
    wellbeing_score INT,
    -- Range: 0-100
    last_assessment_date DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX unique_email (email),
    INDEX idx_role (role),
    INDEX idx_active (active)
);

-- =====================================================
-- 2. APPOINTMENTS TABLE
-- =====================================================
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    professional_id BIGINT NOT NULL,
    appointment_date DATETIME NOT NULL,
    status VARCHAR(50),
    -- Enum values: SCHEDULED, COMPLETED, CANCELLED
    notes LONGTEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (professional_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id),
    INDEX idx_professional_id (professional_id),
    INDEX idx_status (status),
    INDEX idx_appointment_date (appointment_date)
);

-- =====================================================
-- 3. ASSESSMENTS TABLE
-- =====================================================
CREATE TABLE assessments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255),
    description LONGTEXT,
    content LONGTEXT,
    completed_at DATETIME NOT NULL,
    answers VARCHAR(255),
    -- Comma-separated answers (e.g., "2,1,3,2,1,0,2,3")
    total_score INT NOT NULL,
    category VARCHAR(50),
    -- Values: LOW, MILD, MODERATE, SEVERE
    q1_score INT,
    q2_score INT,
    q3_score INT,
    q4_score INT,
    q5_score INT,
    q6_score INT,
    q7_score INT,
    q8_score INT,
    -- Legacy fields
    score INT,
    result VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_completed_at (completed_at),
    INDEX idx_category (category)
);

-- =====================================================
-- 4. EDUCATIONAL_MODULES TABLE
-- =====================================================
CREATE TABLE educational_modules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description LONGTEXT NOT NULL,
    duration_minutes INT NOT NULL,
    category VARCHAR(255) NOT NULL,
    -- Values: Foundation, Coping Skills, Personal Growth, Wellness
    image_url VARCHAR(255),
    content LONGTEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_active (active),
    INDEX idx_category (category)
);

-- =====================================================
-- 5. MODULE_PROGRESS TABLE
-- =====================================================
CREATE TABLE module_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    module_id BIGINT NOT NULL,
    completed BOOLEAN DEFAULT FALSE,
    progress_percentage INT DEFAULT 0,
    -- Range: 0-100
    started_at DATETIME,
    completed_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (module_id) REFERENCES educational_modules(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_module_id (module_id),
    INDEX idx_completed (completed),
    UNIQUE INDEX unique_user_module (user_id, module_id)
);

-- =====================================================
-- 6. CONTENT TABLE
-- =====================================================
CREATE TABLE content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    -- Enum values: ARTICLE, VIDEO, RESOURCE, MODULE
    category VARCHAR(255) NOT NULL,
    description LONGTEXT NOT NULL,
    content LONGTEXT,
    url VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    -- Enum values: PUBLISHED, DRAFT
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_category (category)
);

-- =====================================================
-- 7. FORUM_POSTS TABLE
-- =====================================================
CREATE TABLE forum_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content LONGTEXT NOT NULL,
    category VARCHAR(255),
    -- Values: Academic Stress, Anxiety, Depression, Self-Care, Motivation, General
    views INT DEFAULT 0,
    replies INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_category (category),
    INDEX idx_created_at (created_at)
);

-- =====================================================
-- 8. FORUM_COMMENTS TABLE
-- =====================================================
CREATE TABLE forum_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content LONGTEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES forum_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
);

-- =====================================================
-- 9. SELF_CARE TABLE
-- =====================================================
CREATE TABLE self_care (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    -- Enum values: MOOD, MEDITATION, BREATHING, EXERCISE, MUSIC
    activity_date DATE NOT NULL,
    mood VARCHAR(50),
    -- Values: great, good, okay, low, struggling
    activity_title VARCHAR(255),
    duration_minutes INT,
    notes LONGTEXT,
    breath_duration INT,
    breathing_technique VARCHAR(255),
    -- Values: Box Breathing, 4-7-8 Breathing, Belly Breathing
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_activity_date (activity_date)
);

-- =====================================================
-- 10. REPORTS TABLE
-- =====================================================
CREATE TABLE reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    type VARCHAR(255),
    description LONGTEXT,
    status VARCHAR(50) DEFAULT 'pending',
    -- Values: pending, in_progress, resolved, closed
    urgency VARCHAR(50),
    -- Values: low, medium, high, critical
    stress_score INT,
    anxiety_score INT,
    depression_score INT,
    submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_student_id (student_id),
    INDEX idx_status (status),
    INDEX idx_urgency (urgency),
    INDEX idx_submitted_at (submitted_at)
);

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================
-- Additional indexes for common queries
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_appointments_created_at ON appointments(created_at);
CREATE INDEX idx_assessments_created_at ON assessments(created_at);
CREATE INDEX idx_module_progress_created_at ON module_progress(created_at);
CREATE INDEX idx_content_created_at ON content(created_at);
CREATE INDEX idx_forum_posts_views ON forum_posts(views);
CREATE INDEX idx_self_care_created_at ON self_care(created_at);

-- =====================================================
-- SAMPLE DATA (Optional - Comment out if not needed)
-- =====================================================

-- Insert sample users
INSERT INTO users (email, password, name, role, active, stress_level, anxiety_level, wellbeing_score)
VALUES 
    ('admin@mentalhealthhub.com', 'hashed_password_admin', 'Administrator', 'ADMIN', TRUE, NULL, NULL, NULL),
    ('professional@mentalhealthhub.com', 'hashed_password_prof', 'Dr. Jane Smith', 'PROFESSIONAL', TRUE, NULL, NULL, NULL),
    ('staff@mentalhealthhub.com', 'hashed_password_staff', 'Staff Member', 'STAFF', TRUE, NULL, NULL, NULL),
    ('student1@university.edu', 'hashed_password_1', 'John Student', 'STUDENT', TRUE, 45, 35, 65),
    ('student2@university.edu', 'hashed_password_2', 'Sarah Johnson', 'STUDENT', TRUE, 55, 40, 60);

-- Insert sample educational modules
INSERT INTO educational_modules (title, description, duration_minutes, category, active)
VALUES 
    ('Introduction to Mental Health', 'Understand the basics of mental health and wellness', 30, 'Foundation', TRUE),
    ('Stress Management Techniques', 'Learn effective ways to manage and reduce stress', 45, 'Coping Skills', TRUE),
    ('Mindfulness and Meditation', 'Develop mindfulness skills through guided exercises', 60, 'Personal Growth', TRUE),
    ('Building Healthy Habits', 'Create sustainable wellness routines', 50, 'Wellness', TRUE);

-- Insert sample content
INSERT INTO content (title, type, category, description, status)
VALUES 
    ('10 Ways to Manage Anxiety', 'ARTICLE', 'Anxiety Management', 'Practical tips and strategies for managing anxiety', 'PUBLISHED'),
    ('Mindfulness for Beginners', 'VIDEO', 'Meditation', 'An introductory video to mindfulness practices', 'PUBLISHED'),
    ('Mental Health Resource Guide', 'RESOURCE', 'General', 'Comprehensive guide to mental health resources', 'PUBLISHED');

-- Insert sample forum posts
INSERT INTO forum_posts (user_id, title, content, category, views)
VALUES 
    (4, 'How do you manage exam stress?', 'I am struggling with exam anxiety. What techniques have worked for you?', 'Academic Stress', 12),
    (5, 'Finding time for self-care', 'How do you balance studying with self-care activities?', 'Self-Care', 8);

-- Insert sample self-care entries
INSERT INTO self_care (user_id, type, activity_date, mood, activity_title, duration_minutes)
VALUES 
    (4, 'MOOD', CURDATE(), 'good', NULL, NULL),
    (5, 'MEDITATION', CURDATE(), 'great', 'Morning Meditation', 10),
    (4, 'EXERCISE', CURDATE(), 'good', 'Morning Run', 30);

-- =====================================================
-- Verify table creation
-- =====================================================
-- Run these SELECT statements to verify tables were created:
-- SHOW TABLES;
-- DESCRIBE users;
-- DESCRIBE appointments;
-- DESCRIBE assessments;
-- DESCRIBE educational_modules;
-- DESCRIBE module_progress;
-- DESCRIBE content;
-- DESCRIBE forum_posts;
-- DESCRIBE forum_comments;
-- DESCRIBE self_care;
-- DESCRIBE reports;
