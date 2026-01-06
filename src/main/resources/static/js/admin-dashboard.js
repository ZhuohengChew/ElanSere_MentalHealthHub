// Admin Dashboard JavaScript - Load real data from analytics API

// Load all analytics on document ready
document.addEventListener('DOMContentLoaded', function() {
    loadAdminDashboard();
});

// Main function to load admin dashboard data
async function loadAdminDashboard() {
    try {
        const response = await fetch(buildApiUrl('/api/analytics/comprehensive'));
        if (!response.ok) throw new Error('Failed to fetch analytics data');
        
        const data = await response.json();
        
        // Populate admin dashboard metrics
        populateAdminDashboardMetrics(data);
        
    } catch (error) {
        console.error('Error loading admin dashboard:', error);
        showError('Failed to load admin dashboard data');
    }
}

// Populate the admin dashboard stat cards with real data
function populateAdminDashboardMetrics(data) {
    try {
        // Total Users
        if (data.userAnalytics) {
            const totalUsers = data.userAnalytics.totalUsers || 0;
            const newUsersMonth = data.userAnalytics.newUsersLast30Days || 0;
            
            updateStatCard('adminTotalUsers', formatNumber(totalUsers));
            updateStatCard('adminNewUsersMonth', '+' + formatNumber(newUsersMonth));
        }
        
        // Total Appointments (all time)
        if (data.appointmentAnalytics) {
            const totalAppointments = data.appointmentAnalytics.totalAppointments || 0;
            updateStatCard('adminTotalAppointments', formatNumber(totalAppointments));
        }
        
        // Average Wellbeing (System Health)
        if (data.mentalHealthTrends) {
            const avgWellbeing = data.mentalHealthTrends.averageWellbeingScore || 0;
            updateStatCard('adminAverageWellbeing', (avgWellbeing).toFixed(2));
        }
        
        // Average Engagement Rate
        // Calculate from engagement rates list
        if (data.engagementList) {
            const avgEngagement = calculateAverageEngagement(data.engagementList);
            updateStatCard('adminAverageEngagement', (avgEngagement).toFixed(2) + '%');
        }
        
    } catch (error) {
        console.error('Error populating admin dashboard metrics', error);
    }
}

// Calculate average engagement rate from the engagement list
function calculateAverageEngagement(engagementList) {
    if (!engagementList || engagementList.length === 0) {
        return 0;
    }
    
    const sum = engagementList.reduce((acc, item) => {
        return acc + (Number(item.rate) || 0);
    }, 0);
    
    return sum / engagementList.length;
}

// Helper function to update stat card content
function updateStatCard(elementId, value) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = value;
    }
}

// Format numbers with thousand separators
function formatNumber(num) {
    if (!num) return '0';
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

// Show error notification
function showError(message) {
    console.error(message);
    alert(message);
}

// Build API URL with proper context path
function buildApiUrl(path) {
    try {
        const url = new URL(path, window.location.origin);
        if (url.origin === window.location.origin && path.startsWith('/')) {
            const parts = window.location.pathname.split('/').filter(p => p.length > 0);
            if (parts.length > 0) {
                const context = '/' + parts[0];
                return window.location.origin + context + path;
            }
        }
    } catch (e) {
        // ignore and fallback
    }
    return path;
}
