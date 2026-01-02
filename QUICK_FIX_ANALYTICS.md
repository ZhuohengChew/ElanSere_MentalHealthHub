# Quick Start: Fix Analytics Dashboard

## TL;DR

Your analytics dashboard is empty because there's **no data** in the assessments table and only 3 records in self-care.

### ✅ Solution: Run This SQL

Open your MySQL/phpMyAdmin and run:
```bash
mysql -h 127.0.0.1 -u root -p mental_health_hub < database/add_analytics_test_data.sql
```

Or in **phpMyAdmin**:
1. Click "SQL" tab
2. Open file: `database/add_analytics_test_data.sql`
3. Copy & paste into query editor
4. Click "Go"

### 🎯 What Gets Fixed

| Chart | Issue | Data Added |
|---|---|---|
| **Score Distribution** | Empty bar chart | 12 assessments across 4 score ranges |
| **Mental Health Trend** | No line chart | Monthly trend data (Nov-Jan) |
| **Self-Care Activities** | No canvas visible | 28 self-care records (5 activity types) |
| **Mood Distribution** | Empty | Mood breakdown (5 mood types) |

### 📊 Expected Results

After running the script:
- **Assessments**: 12 new records (Low, Mild, Moderate, High scores)
- **Self-Care**: 28 new records (Meditation, Exercise, Breathing, Music, Mood)
- All charts will display data immediately upon refresh

### 🔄 Refresh Steps

1. Run the SQL script
2. Go to browser: `http://localhost:8081/admin/analytics`
3. Press `Ctrl+F5` (hard refresh) to clear cache
4. All 4 charts should now show data ✓

### 📁 Files Created

| File | Purpose |
|---|---|
| `database/add_analytics_test_data.sql` | SQL script with test data |
| `ANALYTICS_DATA_SETUP.md` | Detailed setup instructions |
| `ANALYTICS_DATA_ANALYSIS.md` | Technical analysis & visualization guide |

---

**Questions?** Check the detailed guides linked above.
