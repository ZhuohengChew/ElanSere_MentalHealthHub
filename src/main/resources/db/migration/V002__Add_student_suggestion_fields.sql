-- Add student suggestion columns to appointments table (if not exists)
ALTER TABLE appointments 
ADD COLUMN IF NOT EXISTS suggested_appointment_date DATE NULL,
ADD COLUMN IF NOT EXISTS suggested_time_slot_start TIME NULL,
ADD COLUMN IF NOT EXISTS suggested_time_slot_end TIME NULL;

-- Modify the status ENUM to include STUDENT_PROPOSED
ALTER TABLE appointments 
MODIFY COLUMN status ENUM('PENDING','APPROVED','REJECTED','STUDENT_PROPOSED') DEFAULT NULL;

