// Analytics Dashboard JavaScript - Comprehensive Charts and Data Visualization

// Color scheme matching the theme
const THEME_COLORS = {
    primary: '#6A8EAE',
    secondary: '#7FB685',
    accent: '#9B7EDE',
    warning: '#FF9F1C',
    danger: '#DC3545',
    success: '#4CAF50',
    info: '#2196F3',
    light: '#F5F5F5'
};

// Chart instances storage
const charts = {};

// Load all analytics on document ready
document.addEventListener('DOMContentLoaded', function() {
    // Load Chart.js library if not present
    if (!window.Chart) {
        const script = document.createElement('script');
        script.src = 'https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js';
        script.onload = loadAnalytics;
        document.head.appendChild(script);
    } else {
        loadAnalytics();
    }
});

// Main function to load all analytics
async function loadAnalytics() {
    try {
        const response = await fetch(buildApiUrl('/api/analytics/comprehensive'));
        if (!response.ok) throw new Error('Failed to fetch analytics data');
        
        const data = await response.json();
        
        // Populate all sections
        populateUserAnalytics(data.userAnalytics);
        populateMentalHealthTrends(data.mentalHealthTrends);
        populateAppointmentAnalytics(data.appointmentAnalytics);
        populateModuleAnalytics(data.moduleAnalytics);
        populateSelfCareAnalytics(data.selfCareAnalytics);
        populateForumAnalytics(data.forumAnalytics);
        populateReportAnalytics(data.reportAnalytics);
        populateAdminActivity(data.adminActivity);
        
    } catch (error) {
        console.error('Error loading analytics:', error);
        showError('Failed to load analytics data');
    }
}

// ==================== USER ANALYTICS ====================
async function populateUserAnalytics(data) {
    if (!data) return;
    
    // Populate stat cards
    updateStatCard('totalUsers', formatNumber(data.totalUsers || 0));
    updateStatCard('newUsersMonth', formatNumber(data.newUsersLast30Days || 0));
    updateStatCard('activeUsers', formatNumber(data.activeUsers || 0));
    updateStatCard('inactiveUsers', formatNumber(data.inactiveUsers || 0));
    updateStatCard('studentCount', formatNumber(data.studentsCount || 0));
    updateStatCard('professionalCount', formatNumber(data.professionalsCount || 0));
    updateStatCard('staffCount', formatNumber(data.staffCount || 0));
    
    // User Distribution Chart (Doughnut)
    if (document.getElementById('userDistributionChart')) {
        charts.userDistribution = new Chart(
            document.getElementById('userDistributionChart'),
            {
                type: 'doughnut',
                data: {
                    labels: ['Students', 'Staff', 'Professionals', 'Admins'],
                    datasets: [{
                        data: [
                            data.studentsCount || 0,
                            data.staffCount || 0,
                            data.professionalsCount || 0,
                            data.adminsCount || 0
                        ],
                        backgroundColor: [
                            THEME_COLORS.primary,
                            THEME_COLORS.secondary,
                            THEME_COLORS.accent,
                            THEME_COLORS.warning
                        ],
                        borderColor: '#fff',
                        borderWidth: 2
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: { position: 'bottom' },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return context.label + ': ' + formatNumber(context.parsed);
                                }
                            }
                        }
                    }
                }
            }
        );
    }
    
    // User Registration Trend Chart
    if (document.getElementById('userRegistrationTrendChart')) {
        // This would need a separate API endpoint or data from the main response
        // For now, we'll create a placeholder
        const trendData = await fetchUserRegistrationTrend();
        charts.userTrend = createLineChart('userRegistrationTrendChart', trendData);
    }
}

// ==================== MENTAL HEALTH TRENDS ====================
async function populateMentalHealthTrends(data) {
    if (!data) return;
    
    // Populate stat cards
    updateStatCard('avgStressLevel', (data.averageStressLevel || 0).toFixed(1));
    updateStatCard('avgAnxietyLevel', (data.averageAnxietyLevel || 0).toFixed(1));
    updateStatCard('avgWellbeing', (data.averageWellbeingScore || 0).toFixed(1));
    
    // Mental Health Distribution Chart (Bar)
    if (document.getElementById('mentalHealthDistributionChart')) {
        charts.mhDistribution = new Chart(
            document.getElementById('mentalHealthDistributionChart'),
            {
                type: 'bar',
                data: {
                    labels: ['Low (0-20)', 'Mild (21-40)', 'Moderate (41-60)', 'High (61-100)'],
                    datasets: [{
                        label: 'Number of Students',
                        data: [
                            data.lowScore || 0,
                            data.mildScore || 0,
                            data.moderateScore || 0,
                            data.highScore || 0
                        ],
                        backgroundColor: [
                            '#4CAF50',
                            '#FFC107',
                            '#FF9800',
                            '#F44336'
                        ],
                        borderColor: '#fff',
                        borderWidth: 1
                    }]
                },
                options: {
                    indexAxis: 'x',
                    responsive: true,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return formatNumber(context.parsed.y) + ' students';
                                }
                            }
                        }
                    },
                    scales: {
                        y: { beginAtZero: true }
                    }
                }
            }
        );
    }
    
    // Mental Health Trend Chart (Line)
    if (document.getElementById('mentalHealthTrendChart') && data.trendData) {
        const labels = data.trendData.map(d => d.month);
        const scores = data.trendData.map(d => (d.averageScore || 0).toFixed(1));
        
        charts.mhTrend = new Chart(
            document.getElementById('mentalHealthTrendChart'),
            {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Average Mental Health Score',
                        data: scores,
                        borderColor: THEME_COLORS.danger,
                        backgroundColor: 'rgba(220, 53, 69, 0.1)',
                        tension: 0.3,
                        fill: true,
                        pointRadius: 5,
                        pointBackgroundColor: THEME_COLORS.danger,
                        pointBorderColor: '#fff',
                        pointBorderWidth: 2
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { position: 'bottom' },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return 'Score: ' + context.parsed.y.toFixed(1);
                                }
                            }
                        }
                    },
                    scales: {
                        y: { 
                            beginAtZero: true,
                            max: 100
                        }
                    }
                }
            }
        );
    }
}

// ==================== APPOINTMENT ANALYTICS ====================
async function populateAppointmentAnalytics(data) {
    if (!data) return;
    
    const total = data.totalAppointments || 0;
    
    // Populate stat cards
    updateStatCard('totalAppointments', formatNumber(total));
    updateStatCard('completedAppointments', formatNumber(data.completedAppointments || 0));
    updateStatCard('scheduledAppointments', formatNumber(data.scheduledAppointments || 0));
    updateStatCard('cancelledAppointments', formatNumber(data.cancelledAppointments || 0));
    
    // Calculate percentages
    if (total > 0) {
        updateStatCard('completedPercent', Math.round((data.completedAppointments / total) * 100));
        updateStatCard('scheduledPercent', Math.round((data.scheduledAppointments / total) * 100));
        updateStatCard('cancelledPercent', Math.round((data.cancelledAppointments / total) * 100));
    }
    
    // Appointment Status Chart
    if (document.getElementById('appointmentStatusChart')) {
        charts.appointmentStatus = new Chart(
            document.getElementById('appointmentStatusChart'),
            {
                type: 'doughnut',
                data: {
                    labels: ['Completed', 'Scheduled', 'Cancelled'],
                    datasets: [{
                        data: [
                            data.completedAppointments || 0,
                            data.scheduledAppointments || 0,
                            data.cancelledAppointments || 0
                        ],
                        backgroundColor: [
                            THEME_COLORS.success,
                            THEME_COLORS.warning,
                            THEME_COLORS.danger
                        ],
                        borderColor: '#fff',
                        borderWidth: 2
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { position: 'bottom' },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    const percent = total > 0 ? ((context.parsed / total) * 100).toFixed(1) : 0;
                                    return context.label + ': ' + formatNumber(context.parsed) + ' (' + percent + '%)';
                                }
                            }
                        }
                    }
                }
            }
        );
    }
    
    // Monthly Appointment Trend Chart
    if (document.getElementById('appointmentTrendChart') && data.monthlyData) {
        const labels = data.monthlyData.map(d => d.month);
        const counts = data.monthlyData.map(d => d.count);
        
        charts.appointmentTrend = new Chart(
            document.getElementById('appointmentTrendChart'),
            {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Completed Appointments',
                        data: counts,
                        backgroundColor: THEME_COLORS.success,
                        borderColor: THEME_COLORS.secondary,
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return formatNumber(context.parsed.y) + ' appointments';
                                }
                            }
                        }
                    },
                    scales: {
                        y: { beginAtZero: true }
                    }
                }
            }
        );
    }
    
    // Professional Workload Chart
    if (document.getElementById('professionalWorkloadChart') && data.professionalWorkloads) {
        const names = data.professionalWorkloads.slice(0, 10).map(p => p.professionalName);
        const counts = data.professionalWorkloads.slice(0, 10).map(p => p.appointmentCount);
        
        charts.professionalWorkload = new Chart(
            document.getElementById('professionalWorkloadChart'),
            {
                type: 'bar',
                data: {
                    labels: names,
                    datasets: [{
                        label: 'Appointments',
                        data: counts,
                        backgroundColor: THEME_COLORS.accent,
                        borderColor: THEME_COLORS.secondary,
                        borderWidth: 1
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return formatNumber(context.parsed.x) + ' appointments';
                                }
                            }
                        }
                    },
                    scales: {
                        x: { beginAtZero: true }
                    }
                }
            }
        );
    }
}

// ==================== MODULE ANALYTICS ====================
async function populateModuleAnalytics(data) {
    if (!data) return;
    
    // Populate stat cards
    updateStatCard('avgModuleProgress', (data.averageProgressPercentage || 0).toFixed(1) + '%');
    updateStatCard('totalModuleCompletions', formatNumber(data.totalCompletions || 0));
    
    // Module Completion Trend Chart
    if (document.getElementById('moduleCompletionTrendChart') && data.monthlyCompletions) {
        const labels = data.monthlyCompletions.map(d => d.month);
        const counts = data.monthlyCompletions.map(d => d.count);
        
        charts.moduleCompletion = new Chart(
            document.getElementById('moduleCompletionTrendChart'),
            {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Module Completions',
                        data: counts,
                        borderColor: THEME_COLORS.secondary,
                        backgroundColor: 'rgba(127, 182, 133, 0.1)',
                        tension: 0.3,
                        fill: true,
                        pointRadius: 4,
                        pointBackgroundColor: THEME_COLORS.secondary,
                        pointBorderColor: '#fff',
                        pointBorderWidth: 2
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return formatNumber(context.parsed.y) + ' completions';
                                }
                            }
                        }
                    },
                    scales: {
                        y: { beginAtZero: true }
                    }
                }
            }
        );
    }
    
    // Most Accessed Modules Chart
    if (document.getElementById('moduleAccessChart') && data.mostAccessedModules) {
        const titles = data.mostAccessedModules.map(m => truncateText(m.title, 20));
        const completions = data.mostAccessedModules.map(m => m.completions);
        
        charts.moduleAccess = new Chart(
            document.getElementById('moduleAccessChart'),
            {
                type: 'bar',
                data: {
                    labels: titles,
                    datasets: [{
                        label: 'Completions',
                        data: completions,
                        backgroundColor: THEME_COLORS.info,
                        borderColor: THEME_COLORS.primary,
                        borderWidth: 1
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return formatNumber(context.parsed.x) + ' completions';
                                }
                            }
                        }
                    },
                    scales: {
                        x: { beginAtZero: true }
                    }
                }
            }
        );
    }
}

// ==================== SELF-CARE ANALYTICS ====================
async function populateSelfCareAnalytics(data) {
    if (!data) return;
    
    // Self-Care Activity Distribution Chart
    if (document.getElementById('selfCareActivityChart') && data.activityDistribution) {
        const activities = data.activityDistribution.map(a => a.activity);
        const counts = data.activityDistribution.map(a => a.count);
        
        charts.selfCareActivity = new Chart(
            document.getElementById('selfCareActivityChart'),
            {
                type: 'doughnut',
                data: {
                    labels: activities,
                    datasets: [{
                        data: counts,
                        backgroundColor: [
                            THEME_COLORS.primary,
                            THEME_COLORS.secondary,
                            THEME_COLORS.accent,
                            THEME_COLORS.warning,
                            THEME_COLORS.info,
                            THEME_COLORS.danger
                        ],
                        borderColor: '#fff',
                        borderWidth: 2
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { position: 'bottom' },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return context.label + ': ' + formatNumber(context.parsed);
                                }
                            }
                        }
                    }
                }
            }
        );
    }
    
    // Mood Distribution Chart
    if (document.getElementById('moodDistributionChart') && data.moodDistribution) {
        const moods = Object.keys(data.moodDistribution);
        const moodCounts = Object.values(data.moodDistribution);
        
        const moodColors = {
            'great': '#4CAF50',
            'good': '#8BC34A',
            'okay': '#FFC107',
            'low': '#FF9800',
            'struggling': '#F44336'
        };
        
        charts.moodDistribution = new Chart(
            document.getElementById('moodDistributionChart'),
            {
                type: 'bar',
                data: {
                    labels: moods.map(m => m.charAt(0).toUpperCase() + m.slice(1)),
                    datasets: [{
                        label: 'Number of Entries',
                        data: moodCounts,
                        backgroundColor: moods.map(m => moodColors[m] || THEME_COLORS.primary),
                        borderColor: '#fff',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return formatNumber(context.parsed.y) + ' entries';
                                }
                            }
                        }
                    },
                    scales: {
                        y: { beginAtZero: true }
                    }
                }
            }
        );
    }
}

// ==================== FORUM ANALYTICS ====================
async function populateForumAnalytics(data) {
    if (!data) return;
    
    // Populate stat cards
    updateStatCard('totalForumPosts', formatNumber(data.totalPosts || 0));
    updateStatCard('totalForumComments', formatNumber(data.totalComments || 0));
    updateStatCard('avgViewsPerPost', (data.averageViewsPerPost || 0).toFixed(1));
    updateStatCard('avgRepliesPerPost', (data.averageRepliesPerPost || 0).toFixed(1));
    
    // Forum Category Chart
    if (document.getElementById('forumCategoryChart') && data.categoryStats) {
        const categories = data.categoryStats.map(c => c.category);
        const postCounts = data.categoryStats.map(c => c.postCount);
        
        charts.forumCategory = new Chart(
            document.getElementById('forumCategoryChart'),
            {
                type: 'bar',
                data: {
                    labels: categories,
                    datasets: [{
                        label: 'Number of Posts',
                        data: postCounts,
                        backgroundColor: THEME_COLORS.primary,
                        borderColor: THEME_COLORS.secondary,
                        borderWidth: 1
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return formatNumber(context.parsed.x) + ' posts';
                                }
                            }
                        }
                    },
                    scales: {
                        x: { beginAtZero: true }
                    }
                }
            }
        );
    }
    
    // Active Users Chart
    if (document.getElementById('activeUsersChart') && data.activeUsers) {
        const userNames = data.activeUsers.map(u => truncateText(u.userName, 15));
        const postCounts = data.activeUsers.map(u => u.postCount);
        
        charts.activeUsers = new Chart(
            document.getElementById('activeUsersChart'),
            {
                type: 'bar',
                data: {
                    labels: userNames,
                    datasets: [{
                        label: 'Posts',
                        data: postCounts,
                        backgroundColor: THEME_COLORS.accent,
                        borderColor: THEME_COLORS.secondary,
                        borderWidth: 1
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return formatNumber(context.parsed.x) + ' posts';
                                }
                            }
                        }
                    },
                    scales: {
                        x: { beginAtZero: true }
                    }
                }
            }
        );
    }
}

// ==================== REPORT ANALYTICS ====================
async function populateReportAnalytics(data) {
    if (!data) return;
    
    // Populate stat cards
    updateStatCard('totalReports', formatNumber(data.totalReports || 0));
    updateStatCard('pendingReports', formatNumber(data.pendingReports || 0));
    updateStatCard('resolvedReports', formatNumber(data.resolvedReports || 0));
    updateStatCard('avgResolutionTime', (data.averageResolutionTimeHours || 0).toFixed(1));
    updateStatCard('totalAssessments', formatNumber(data.totalCompletions || 0));
    
    // Report Status Chart
    if (document.getElementById('reportStatusChart')) {
        charts.reportStatus = new Chart(
            document.getElementById('reportStatusChart'),
            {
                type: 'doughnut',
                data: {
                    labels: ['Pending', 'In Progress', 'Resolved', 'Closed'],
                    datasets: [{
                        data: [
                            data.pendingReports || 0,
                            data.inProgressReports || 0,
                            data.resolvedReports || 0,
                            data.closedReports || 0
                        ],
                        backgroundColor: [
                            THEME_COLORS.warning,
                            THEME_COLORS.info,
                            THEME_COLORS.success,
                            THEME_COLORS.secondary
                        ],
                        borderColor: '#fff',
                        borderWidth: 2
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { position: 'bottom' },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return context.label + ': ' + formatNumber(context.parsed);
                                }
                            }
                        }
                    }
                }
            }
        );
    }
    
    // Report Urgency Chart
    if (document.getElementById('reportUrgencyChart')) {
        charts.reportUrgency = new Chart(
            document.getElementById('reportUrgencyChart'),
            {
                type: 'bar',
                data: {
                    labels: ['Low', 'Medium', 'High', 'Critical'],
                    datasets: [{
                        label: 'Number of Reports',
                        data: [
                            data.lowUrgency || 0,
                            data.mediumUrgency || 0,
                            data.highUrgency || 0,
                            data.criticalUrgency || 0
                        ],
                        backgroundColor: [
                            '#4CAF50',
                            '#FFC107',
                            '#FF9800',
                            '#F44336'
                        ],
                        borderColor: '#fff',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return formatNumber(context.parsed.y) + ' reports';
                                }
                            }
                        }
                    },
                    scales: {
                        y: { beginAtZero: true }
                    }
                }
            }
        );
    }
    
    // Monthly Report Trend Chart
    if (document.getElementById('reportTrendChart') && data.monthlyTrend) {
        const labels = data.monthlyTrend.map(d => d.month);
        const submissions = data.monthlyTrend.map(d => d.count);
        const resolutions = data.monthlyResolutions ? data.monthlyResolutions.map(d => d.count) : [];
        
        charts.reportTrend = new Chart(
            document.getElementById('reportTrendChart'),
            {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: 'Submitted',
                            data: submissions,
                            borderColor: THEME_COLORS.danger,
                            backgroundColor: 'rgba(220, 53, 69, 0.1)',
                            tension: 0.3,
                            fill: true,
                            pointRadius: 4,
                            pointBackgroundColor: THEME_COLORS.danger,
                            borderWidth: 2
                        },
                        {
                            label: 'Resolved',
                            data: resolutions,
                            borderColor: THEME_COLORS.success,
                            backgroundColor: 'rgba(76, 175, 80, 0.1)',
                            tension: 0.3,
                            fill: true,
                            pointRadius: 4,
                            pointBackgroundColor: THEME_COLORS.success,
                            borderWidth: 2
                        }
                    ]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { position: 'bottom' },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return context.dataset.label + ': ' + formatNumber(context.parsed.y);
                                }
                            }
                        }
                    },
                    scales: {
                        y: { beginAtZero: true }
                    }
                }
            }
        );
    }
}

// ==================== ADMIN ACTIVITY ====================
async function populateAdminActivity(data) {
    if (!data) return;
    
    // Populate stat cards
    updateStatCard('actionsThisWeek', formatNumber(data.totalActionsThisWeek || 0));
    
    // Admin Activity Chart
    if (document.getElementById('adminActivityChart') && data.adminActivity) {
        const adminNames = data.adminActivity.map(a => truncateText(a.adminName, 15));
        const actionCounts = data.adminActivity.map(a => a.actionCount);
        
        charts.adminActivity = new Chart(
            document.getElementById('adminActivityChart'),
            {
                type: 'bar',
                data: {
                    labels: adminNames,
                    datasets: [{
                        label: 'Actions',
                        data: actionCounts,
                        backgroundColor: THEME_COLORS.primary,
                        borderColor: THEME_COLORS.secondary,
                        borderWidth: 1
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return formatNumber(context.parsed.x) + ' actions';
                                }
                            }
                        }
                    },
                    scales: {
                        x: { beginAtZero: true }
                    }
                }
            }
        );
    }
}

// ==================== UTILITY FUNCTIONS ====================

// Update stat card values
function updateStatCard(elementId, value) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = value;
    }
}

// Format numbers with thousand separators
function formatNumber(num) {
    if (num === null || num === undefined) return '0';
    return Math.round(num).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

// Truncate text with ellipsis
function truncateText(text, length) {
    if (!text) return '';
    return text.length > length ? text.substring(0, length) + '...' : text;
}

// Create line chart helper
function createLineChart(canvasId, data) {
    const canvas = document.getElementById(canvasId);
    if (!canvas || !data) return null;
    
    return new Chart(canvas, {
        type: 'line',
        data: data,
        options: {
            responsive: true,
            plugins: {
                tooltip: {
                    mode: 'index',
                    intersect: false
                }
            }
        }
    });
}

// Fetch user registration trend data
async function fetchUserRegistrationTrend() {
    try {
        const response = await fetch(buildApiUrl('/api/analytics/users'));
        const data = await response.json();
        // Parse data into chart format
        return {
            labels: Object.keys(data.userRegistrationTrend || {}),
            datasets: [{
                label: 'New Users',
                data: Object.values(data.userRegistrationTrend || {}),
                borderColor: THEME_COLORS.secondary,
                backgroundColor: 'rgba(127, 182, 133, 0.1)',
                tension: 0.3,
                fill: true
            }]
        };
    } catch (error) {
        console.error('Error fetching user trend data:', error);
        return null;
    }
}

// Build API URL respecting application context path (e.g. /mentalhealthhub)
function buildApiUrl(path) {
    // If path already absolute with origin, return as-is
    try {
        const url = new URL(path, window.location.origin);
        if (url.origin === window.location.origin && path.startsWith('/')) {
            // Need to insert context path if present in the current pathname
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

// Show error notification
function showError(message) {
    console.error(message);
    // You can implement a toast/notification system here
    alert(message);
}

// Export for global access
window.AnalyticsDashboard = {
    loadAnalytics,
    formatNumber,
    truncateText
};
