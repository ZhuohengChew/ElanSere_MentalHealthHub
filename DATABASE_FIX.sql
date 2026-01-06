-- Manual SQL Fix for Registration Trend
-- Run this directly in your database (phpMyAdmin or MySQL client)

-- First, check what created_at values currently exist
SELECT id, name, created_at, updated_at FROM users ORDER BY id;

-- Then run these updates to populate the created_at dates:
UPDATE users SET created_at = '2025-08-15 10:30:00', updated_at = '2025-08-15 10:30:00' WHERE id = 1;
UPDATE users SET created_at = '2025-09-20 14:15:00', updated_at = '2025-09-20 14:15:00' WHERE id = 2;
UPDATE users SET created_at = '2025-10-10 09:45:00', updated_at = '2025-10-10 09:45:00' WHERE id = 3;
UPDATE users SET created_at = '2025-11-05 16:20:00', updated_at = '2025-11-05 16:20:00' WHERE id = 4;
UPDATE users SET created_at = '2025-12-01 11:00:00', updated_at = '2025-12-01 11:00:00' WHERE id = 5;

-- Verify the update worked:
SELECT id, name, DATE_FORMAT(created_at, '%Y-%m') as month, created_at FROM users WHERE id IN (1,2,3,4,5);

-- Test the exact query used by the application:
SELECT DATE_FORMAT(u.created_at, '%Y-%m') as month, COUNT(u.id) as count 
FROM users u 
WHERE u.deleted_at IS NULL 
GROUP BY DATE_FORMAT(u.created_at, '%Y-%m') 
ORDER BY month DESC;
