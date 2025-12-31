-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 28, 2025 at 10:55 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `mental_health_hub`
--

-- --------------------------------------------------------

--
-- Table structure for table `appointments`
--

CREATE TABLE `appointments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `student_id` bigint(20) NOT NULL,
  `professional_id` bigint(20) NOT NULL,
  `appointment_date` date NOT NULL,
  `time_slot_start` time NOT NULL,
  `time_slot_end` time NOT NULL,
  `status` varchar(20) DEFAULT 'PENDING',
  `notes` longtext DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (professional_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_student_id (student_id),
  INDEX idx_professional_id (professional_id),
  INDEX idx_status (status),
  INDEX idx_appointment_date (appointment_date),
  INDEX idx_appointments_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `assessments`
--

CREATE TABLE `assessments` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `description` longtext DEFAULT NULL,
  `content` longtext DEFAULT NULL,
  `completed_at` datetime NOT NULL,
  `answers` varchar(255) DEFAULT NULL,
  `total_score` int(11) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `q1_score` int(11) DEFAULT NULL,
  `q2_score` int(11) DEFAULT NULL,
  `q3_score` int(11) DEFAULT NULL,
  `q4_score` int(11) DEFAULT NULL,
  `q5_score` int(11) DEFAULT NULL,
  `q6_score` int(11) DEFAULT NULL,
  `q7_score` int(11) DEFAULT NULL,
  `q8_score` int(11) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `result` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `q1score` int(11) DEFAULT NULL,
  `q2score` int(11) DEFAULT NULL,
  `q3score` int(11) DEFAULT NULL,
  `q4score` int(11) DEFAULT NULL,
  `q5score` int(11) DEFAULT NULL,
  `q6score` int(11) DEFAULT NULL,
  `q7score` int(11) DEFAULT NULL,
  `q8score` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `audit_logs`
--

CREATE TABLE `audit_logs` (
  `id` bigint(20) NOT NULL,
  `performed_by` bigint(20) NOT NULL,
  `action` varchar(100) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `details` longtext DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `content`
--

CREATE TABLE `content` (
  `id` bigint(20) NOT NULL,
  `title` varchar(255) NOT NULL,
  `type` enum('ARTICLE','VIDEO','RESOURCE','MODULE') NOT NULL,
  `category` varchar(255) NOT NULL,
  `description` longtext NOT NULL,
  `content` longtext DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `status` enum('PUBLISHED','DRAFT') NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `content`
--

INSERT INTO `content` (`id`, `title`, `type`, `category`, `description`, `content`, `url`, `status`, `created_at`, `updated_at`) VALUES
(1, '10 Ways to Manage Anxiety', 'ARTICLE', 'Anxiety Management', 'Practical tips and strategies for managing anxiety', NULL, NULL, 'PUBLISHED', '2025-12-27 16:03:02', '2025-12-27 16:03:02'),
(2, 'Mindfulness for Beginners', 'VIDEO', 'Meditation', 'An introductory video to mindfulness practices', NULL, NULL, 'PUBLISHED', '2025-12-27 16:03:02', '2025-12-27 16:03:02'),
(3, 'Mental Health Resource Guide', 'RESOURCE', 'General', 'Comprehensive guide to mental health resources', NULL, NULL, 'PUBLISHED', '2025-12-27 16:03:02', '2025-12-27 16:03:02');

-- --------------------------------------------------------

--
-- Table structure for table `educational_modules`
--

CREATE TABLE `educational_modules` (
  `id` bigint(20) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` longtext NOT NULL,
  `duration_minutes` int(11) NOT NULL,
  `category` varchar(255) NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `content` longtext DEFAULT NULL,
  `active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `educational_modules`
--

INSERT INTO `educational_modules` (`id`, `title`, `description`, `duration_minutes`, `category`, `image_url`, `content`, `active`, `created_at`, `updated_at`) VALUES
(1, 'Introduction to Mental Health', 'Understand the basics of mental health and wellness', 30, 'Foundation', NULL, NULL, 1, '2025-12-27 16:03:02', '2025-12-27 16:03:02'),
(2, 'Stress Management Techniques', 'Learn effective ways to manage and reduce stress', 45, 'Coping Skills', NULL, NULL, 1, '2025-12-27 16:03:02', '2025-12-27 16:03:02'),
(3, 'Mindfulness and Meditation', 'Develop mindfulness skills through guided exercises', 60, 'Personal Growth', NULL, NULL, 1, '2025-12-27 16:03:02', '2025-12-27 16:03:02'),
(4, 'Building Healthy Habits', 'Create sustainable wellness routines', 50, 'Wellness', NULL, NULL, 1, '2025-12-27 16:03:02', '2025-12-27 16:03:02');

-- --------------------------------------------------------

--
-- Table structure for table `forum_comments`
--

CREATE TABLE `forum_comments` (
  `id` bigint(20) NOT NULL,
  `post_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `content` longtext NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `forum_posts`
--

CREATE TABLE `forum_posts` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` longtext NOT NULL,
  `category` varchar(255) DEFAULT NULL,
  `views` int(11) DEFAULT 0,
  `replies` int(11) DEFAULT 0,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `forum_posts`
--

INSERT INTO `forum_posts` (`id`, `user_id`, `title`, `content`, `category`, `views`, `replies`, `created_at`, `updated_at`) VALUES
(1, 4, 'How do you manage exam stress?', 'I am struggling with exam anxiety. What techniques have worked for you?', 'Academic Stress', 12, 0, '2025-12-27 16:03:02', '2025-12-27 16:03:02'),
(2, 5, 'Finding time for self-care', 'How do you balance studying with self-care activities?', 'Self-Care', 8, 0, '2025-12-27 16:03:02', '2025-12-27 16:03:02');

-- --------------------------------------------------------

--
-- Table structure for table `module_progress`
--

CREATE TABLE `module_progress` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `module_id` bigint(20) NOT NULL,
  `completed` tinyint(1) DEFAULT 0,
  `progress_percentage` int(11) DEFAULT 0,
  `started_at` datetime DEFAULT NULL,
  `completed_at` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reports`
--

CREATE TABLE `reports` (
  `id` bigint(20) NOT NULL,
  `student_id` bigint(20) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `description` longtext DEFAULT NULL,
  `status` varchar(50) DEFAULT 'pending',
  `urgency` varchar(50) DEFAULT NULL,
  `submitted_at` datetime DEFAULT current_timestamp(),
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `resolution_notes` varchar(255) DEFAULT NULL,
  `resolved_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reports`
--

INSERT INTO `reports` (`id`, `student_id`, `type`, `description`, `status`, `urgency`, `submitted_at`, `created_at`, `updated_at`, `resolution_notes`, `resolved_at`) VALUES
(1, 4, 'Stress or Overwhelm, Self-Harm', 'sdfsa', 'resolved', 'high', '2025-12-27 16:06:48', '2025-12-27 16:06:48', '2025-12-27 22:51:02', 'problem sovled', '2025-12-27 22:51:02.000000'),
(2, 4, 'Stress or Overwhelm, Self-Harm', 'asd', 'reviewed', 'high', '2025-12-27 17:02:09', '2025-12-27 17:02:09', '2025-12-27 22:57:33', NULL, NULL),
(3, 4, 'Depression, Stress', 'adsssssssssssssss', 'reviewed', 'moderate', '2025-12-27 17:05:42', '2025-12-27 17:05:42', '2025-12-27 22:57:37', NULL, NULL),
(4, 4, 'Anxiety or Panic, Stress or Overwhelm', 'dssssss', 'reviewed', 'high', '2025-12-27 19:28:47', '2025-12-27 19:28:47', '2025-12-27 23:02:32', NULL, NULL),
(5, 5, 'Academic Pressure, Eating or Body Image', 'dsfsdfsdf', 'reviewed', 'low', '2025-12-28 14:33:00', '2025-12-28 14:33:00', '2025-12-28 14:51:50', NULL, NULL),
(6, 5, 'Sleep Issues, Suicidal Thoughts', 'sdasdadasdas', 'reviewed', 'moderate', '2025-12-28 14:42:06', '2025-12-28 14:42:06', '2025-12-28 15:27:40', NULL, NULL),
(7, 5, 'Depression, Stress', 'sdfsdfsdfsdfs', 'reviewed', 'moderate', '2025-12-28 14:51:40', '2025-12-28 14:51:40', '2025-12-28 17:30:13', NULL, NULL),
(8, 5, 'Self-Harm, Eating Disorders', 'dasfsdfsdfsd', 'reviewed', 'low', '2025-12-28 15:01:27', '2025-12-28 15:01:27', '2025-12-28 15:29:10', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `self_care`
--

CREATE TABLE `self_care` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `type` enum('MOOD','MEDITATION','BREATHING','EXERCISE','MUSIC') NOT NULL,
  `activity_date` date NOT NULL,
  `mood` varchar(50) DEFAULT NULL,
  `activity_title` varchar(255) DEFAULT NULL,
  `duration_minutes` int(11) DEFAULT NULL,
  `notes` longtext DEFAULT NULL,
  `breath_duration` int(11) DEFAULT NULL,
  `breathing_technique` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `self_care`
--

INSERT INTO `self_care` (`id`, `user_id`, `type`, `activity_date`, `mood`, `activity_title`, `duration_minutes`, `notes`, `breath_duration`, `breathing_technique`, `created_at`, `updated_at`) VALUES
(1, 4, 'MOOD', '2025-12-27', 'good', NULL, NULL, NULL, NULL, NULL, '2025-12-27 16:03:02', '2025-12-27 16:03:02'),
(2, 5, 'MEDITATION', '2025-12-27', 'great', 'Morning Meditation', 10, NULL, NULL, NULL, '2025-12-27 16:03:02', '2025-12-27 16:03:02'),
(3, 4, 'EXERCISE', '2025-12-27', 'good', 'Morning Run', 30, NULL, NULL, NULL, '2025-12-27 16:03:02', '2025-12-27 16:03:02');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `role` enum('STUDENT','STAFF','PROFESSIONAL','ADMIN') NOT NULL,
  `active` tinyint(1) DEFAULT 1,
  `profile_picture` longtext DEFAULT NULL,
  `stress_level` int(11) DEFAULT NULL,
  `anxiety_level` int(11) DEFAULT NULL,
  `wellbeing_score` int(11) DEFAULT NULL,
  `last_assessment_date` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `deleted_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `email`, `password`, `name`, `role`, `active`, `profile_picture`, `stress_level`, `anxiety_level`, `wellbeing_score`, `last_assessment_date`, `created_at`, `updated_at`, `deleted_at`) VALUES
(1, 'admin@mentalhealthhub.com', 'hashed_password_admin', 'Administrator', 'ADMIN', 1, NULL, NULL, NULL, NULL, NULL, '2025-12-27 16:03:02', '2025-12-27 16:03:02', NULL),
(2, 'professional@mentalhealthhub.com', '$2a$10$2eKgGJIJhLlXbXHAcNAchulwe2sxXw4dnUwuZB4XG124RzC.GEpwS', 'Dr. Jane Smith', 'PROFESSIONAL', 1, NULL, NULL, NULL, NULL, NULL, '2025-12-27 16:03:02', '2025-12-27 16:03:02', NULL),
(3, 'staff@mentalhealthhub.com', '$2a$10$kHHrlrmS1EOK0WIJMvjHhuId/SQp5zsWiGxaEuOtYHhKTlFNRz1m2', 'Staff Member', 'STAFF', 1, NULL, NULL, NULL, NULL, NULL, '2025-12-27 16:03:02', '2025-12-27 16:03:02', NULL),
(4, 'student1@university.edu', '$2a$10$X7vc4FMlzH6fmULaPvc3sOnbzp3oXcgciQ1mV03Jt2iT8rhYAypOe', 'John Student', 'STUDENT', 1, NULL, 45, 35, 65, NULL, '2025-12-27 16:03:02', '2025-12-27 16:03:02', NULL),
(5, 'student2@university.edu', '$2a$10$Cfa6YBFYbMBqpqUb3vNIcuDVP580uP8UdS7yMcYrnQpXh1WdhPyK.', 'Sarah Johnson', 'STUDENT', 1, NULL, 55, 40, 60, NULL, '2025-12-27 16:03:02', '2025-12-27 16:03:02', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `appointments`
--
ALTER TABLE `appointments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_student_id` (`student_id`),
  ADD KEY `idx_professional_id` (`professional_id`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_appointment_date` (`appointment_date`),
  ADD KEY `idx_appointments_created_at` (`created_at`);

--
-- Indexes for table `assessments`
--
ALTER TABLE `assessments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_completed_at` (`completed_at`),
  ADD KEY `idx_category` (`category`),
  ADD KEY `idx_assessments_created_at` (`created_at`);

--
-- Indexes for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_performed_by` (`performed_by`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_action` (`action`),
  ADD KEY `idx_created_at` (`created_at`);

--
-- Indexes for table `content`
--
ALTER TABLE `content`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_type` (`type`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_category` (`category`),
  ADD KEY `idx_content_created_at` (`created_at`);

--
-- Indexes for table `educational_modules`
--
ALTER TABLE `educational_modules`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_active` (`active`),
  ADD KEY `idx_category` (`category`);

--
-- Indexes for table `forum_comments`
--
ALTER TABLE `forum_comments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_post_id` (`post_id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_created_at` (`created_at`);

--
-- Indexes for table `forum_posts`
--
ALTER TABLE `forum_posts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_category` (`category`),
  ADD KEY `idx_created_at` (`created_at`),
  ADD KEY `idx_forum_posts_views` (`views`);

--
-- Indexes for table `module_progress`
--
ALTER TABLE `module_progress`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_user_module` (`user_id`,`module_id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_module_id` (`module_id`),
  ADD KEY `idx_completed` (`completed`),
  ADD KEY `idx_module_progress_created_at` (`created_at`);

--
-- Indexes for table `reports`
--
ALTER TABLE `reports`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_student_id` (`student_id`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_urgency` (`urgency`),
  ADD KEY `idx_submitted_at` (`submitted_at`);

--
-- Indexes for table `self_care`
--
ALTER TABLE `self_care`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_type` (`type`),
  ADD KEY `idx_activity_date` (`activity_date`),
  ADD KEY `idx_self_care_created_at` (`created_at`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `unique_email` (`email`),
  ADD KEY `idx_role` (`role`),
  ADD KEY `idx_active` (`active`),
  ADD KEY `idx_users_created_at` (`created_at`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `appointments`
--
ALTER TABLE `appointments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `assessments`
--
ALTER TABLE `assessments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `audit_logs`
--
ALTER TABLE `audit_logs`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `content`
--
ALTER TABLE `content`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `educational_modules`
--
ALTER TABLE `educational_modules`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `forum_comments`
--
ALTER TABLE `forum_comments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `forum_posts`
--
ALTER TABLE `forum_posts`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `module_progress`
--
ALTER TABLE `module_progress`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reports`
--
ALTER TABLE `reports`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `self_care`
--
ALTER TABLE `self_care`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `appointments`
--
ALTER TABLE `appointments`
  ADD CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`professional_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `assessments`
--
ALTER TABLE `assessments`
  ADD CONSTRAINT `assessments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD CONSTRAINT `audit_logs_ibfk_1` FOREIGN KEY (`performed_by`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `audit_logs_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `forum_comments`
--
ALTER TABLE `forum_comments`
  ADD CONSTRAINT `forum_comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `forum_posts` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `forum_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `forum_posts`
--
ALTER TABLE `forum_posts`
  ADD CONSTRAINT `forum_posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `module_progress`
--
ALTER TABLE `module_progress`
  ADD CONSTRAINT `module_progress_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `module_progress_ibfk_2` FOREIGN KEY (`module_id`) REFERENCES `educational_modules` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `reports`
--
ALTER TABLE `reports`
  ADD CONSTRAINT `reports_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `self_care`
--
ALTER TABLE `self_care`
  ADD CONSTRAINT `self_care_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
