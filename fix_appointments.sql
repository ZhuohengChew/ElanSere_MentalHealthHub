-- Drop and recreate appointments table with correct schema
DROP TABLE IF EXISTS appointments;

CREATE TABLE appointments (
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
