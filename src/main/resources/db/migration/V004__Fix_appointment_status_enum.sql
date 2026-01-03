-- Fix appointment status enum to include all status values
-- This migration updates existing SCHEDULED records and adds missing enum values

-- Step 1: Update existing SCHEDULED records to APPROVED (they are functionally the same)
-- Note: This will fail if there are no SCHEDULED records, which is fine
UPDATE appointments SET status = 'APPROVED' WHERE status = 'SCHEDULED';

-- Step 2: Modify the status ENUM to include all possible values
-- This allows the enum to accept SCHEDULED, COMPLETED, and CANCELLED values
ALTER TABLE appointments 
MODIFY COLUMN status ENUM('PENDING','APPROVED','REJECTED','STUDENT_PROPOSED','SCHEDULED','COMPLETED','CANCELLED') DEFAULT NULL;

