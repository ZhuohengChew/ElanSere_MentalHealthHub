-- Populate created_at dates for existing users to show in registration trend
-- This distributes the 5 existing users across the last 6 months for testing

UPDATE users SET created_at = '2025-08-15 10:30:00' WHERE id = 1;  -- Admin: August 2025
UPDATE users SET created_at = '2025-09-20 14:15:00' WHERE id = 2;  -- Professional: September 2025
UPDATE users SET created_at = '2025-10-10 09:45:00' WHERE id = 3;  -- Staff: October 2025
UPDATE users SET created_at = '2025-11-05 16:20:00' WHERE id = 4;  -- Student 1: November 2025
UPDATE users SET created_at = '2025-12-01 11:00:00' WHERE id = 5;  -- Student 2: December 2025

-- Update updated_at to match
UPDATE users SET updated_at = created_at WHERE id IN (1, 2, 3, 4, 5);
