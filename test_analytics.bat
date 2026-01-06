@echo off
REM Test script to check registration trend data

echo Testing database connection...
mysql -h 127.0.0.1 -u root mental_health_hub -e "SELECT DATE_FORMAT(u.created_at, '%%Y-%%m') as month, COUNT(u.id) as count FROM users u WHERE u.deleted_at IS NULL GROUP BY DATE_FORMAT(u.created_at, '%%Y-%%m') ORDER BY month DESC;"

echo.
echo Checking all users created_at dates:
mysql -h 127.0.0.1 -u root mental_health_hub -e "SELECT id, name, created_at FROM users ORDER BY id;"

echo.
echo Done!
