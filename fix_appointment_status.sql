-- Fix appointment status enum issue
-- Run this script manually on your MySQL database

-- Step 1: Update existing SCHEDULED records to APPROVED (they are functionally the same)
UPDATE appointments SET status = 'APPROVED' WHERE status = 'SCHEDULED';

-- Step 2: Update any COMPLETED or CANCELLED if they exist (optional, for safety)
-- UPDATE appointments SET status = 'APPROVED' WHERE status = 'COMPLETED' AND status IS NOT NULL;
-- UPDATE appointments SET status = 'REJECTED' WHERE status = 'CANCELLED' AND status IS NOT NULL;

-- Step 3: Modify the status ENUM to include all possible values
ALTER TABLE appointments 
MODIFY COLUMN status ENUM('PENDING','APPROVED','REJECTED','STUDENT_PROPOSED','SCHEDULED','COMPLETED','CANCELLED') DEFAULT NULL;

