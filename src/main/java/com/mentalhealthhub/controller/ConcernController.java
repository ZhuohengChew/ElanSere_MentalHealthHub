package com.mentalhealthhub.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mentalhealthhub.model.Appointment;
import com.mentalhealthhub.model.AppointmentStatus;
import com.mentalhealthhub.model.Report;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import com.mentalhealthhub.repository.AppointmentRepository;
import com.mentalhealthhub.repository.ReportRepository;
import com.mentalhealthhub.repository.UserRepository;
import com.mentalhealthhub.service.ReportService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for managing concerns and reports
 * Handles CRUD operations, authorization, and reporting for mental health concerns
 */
@Controller
public class ConcernController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportService reportService;

    // ==================== AUTHORIZATION HELPER METHODS ====================
    /**
     * Checks if user is authenticated and is a student
     */
    private boolean isAuthenticatedStudent(User user) {
        return user != null && UserRole.STUDENT.equals(user.getRole());
    }

    /**
     * Checks if user is authenticated and is staff
     */
    private boolean isAuthenticatedStaff(User user) {
        return user != null && UserRole.STAFF.equals(user.getRole());
    }

    /**
     * Checks if user is authenticated and is professional
     */
    private boolean isAuthenticatedProfessional(User user) {
        return user != null && UserRole.PROFESSIONAL.equals(user.getRole());
    }

    /**
     * Checks if user is authenticated and is admin
     */
    private boolean isAuthenticatedAdmin(User user) {
        return user != null && UserRole.ADMIN.equals(user.getRole());
    }

    /**
     * Checks if user can view a specific report
     */
    private boolean canViewReport(User user, Report report) {
        if (user == null || report == null) return false;

        // Student can view their own reports
        if (isAuthenticatedStudent(user) && report.getStudent() != null && 
            report.getStudent().getId().equals(user.getId())) {
            return true;
        }

        // Staff, Professional, and Admin can view all reports
        if (isAuthenticatedStaff(user) || isAuthenticatedProfessional(user) || isAuthenticatedAdmin(user)) {
            return true;
        }

        return false;
    }

    // ==================== STUDENT REPORT SUBMISSION ====================
    /**
     * Display the concern report form for students
     */
    @GetMapping("/concerns")
    public String viewConcernForm(HttpSession session, Model model, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        
        // Authorization: Only authenticated students can access
        if (!isAuthenticatedStudent(user)) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("page", "concerns/report");
        model.addAttribute("title", "Report a Concern");
        model.addAttribute("activePage", "concerns");

        return "layout";
    }

    /**
     * Submit a concern report from student
     */
    @PostMapping("/concerns/submit")
    public String submitConcern(@RequestParam(required = false, name = "concerns") String[] concerns,
                               @RequestParam String description,
                               @RequestParam(required = false) String urgency,
                               HttpSession session, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");

        // Authorization: Only authenticated students can submit reports
        if (!isAuthenticatedStudent(user)) {
            return "redirect:/login";
        }

        // Create report using service
        String type = (concerns != null && concerns.length > 0) 
            ? String.join(", ", concerns) 
            : "Other";

        Report report = reportService.createReport(
            user, type, description, urgency
        );

        // Redirect to confirmation or dashboard
        return "redirect:/concerns/list";
    }

    /**
     * View list of student's own reports
     */
    @GetMapping("/concerns/list")
    public String viewMyReports(HttpSession session, Model model, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        
        // Authorization: Only authenticated users can access
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("page", "concerns/list");
        model.addAttribute("title", "My Reports");
        model.addAttribute("activePage", "concerns");

        return "layout";
    }

    /**
     * API endpoint to get reports for current user
     * Returns different data based on user role
     */
    @GetMapping("/concerns/api/my-reports")
    @ResponseBody
    public ResponseEntity<?> getMyReports(HttpSession session) {
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(List.of());
        }

        List<Report> studentReports;

        if (isAuthenticatedStudent(user)) {
            // Students see only their own reports
            studentReports = reportService.getStudentReports(user.getId());
        } else if (isAuthenticatedStaff(user) || isAuthenticatedProfessional(user)) {
            // Staff and professionals see all reports
            studentReports = reportRepository.findAll();
        } else if (isAuthenticatedAdmin(user)) {
            // Admins see all reports
            studentReports = reportRepository.findAll();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(List.of());
        }

        // Convert to map format
        List<Map<String, Object>> reportData = reportService.reportsToMapList(studentReports);
        return ResponseEntity.ok(reportData);
    }

    /**
     * Get report statistics
     */
    @GetMapping("/concerns/api/statistics")
    @ResponseBody
    public ResponseEntity<?> getStatistics(HttpSession session) {
        User user = (User) session.getAttribute("user");

        // Authorization: Only staff, professionals, and admins can view statistics
        if (!isAuthenticatedStaff(user) && !isAuthenticatedProfessional(user) && !isAuthenticatedAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Map<String, Long> stats = reportService.getReportStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get student-specific statistics
     */
    @GetMapping("/concerns/api/my-statistics")
    @ResponseBody
    public ResponseEntity<?> getMyStatistics(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Map<String, Long> stats = reportService.getStudentReportStatistics(user.getId());
        return ResponseEntity.ok(stats);
    }

    // ==================== REPORT DETAIL VIEW ====================
    /**
     * View detailed information about a specific report
     */
    @GetMapping("/concerns/{id}")
    public String viewReportDetail(@PathVariable Long id, HttpSession session, Model model, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }

        Report report = reportRepository.findById(id).orElse(null);
        if (report == null) {
            return "redirect:/concerns/list";
        }

        // Authorization: Check if user can view this report
        if (!canViewReport(user, report)) {
            return "redirect:/concerns/list";
        }

        // Fetch appointment if report status is scheduled
        Appointment appointment = null;
        if ("scheduled".equals(report.getStatus())) {
            appointment = appointmentRepository.findByReportIdWithProfessional(report.getId());
        }
        
        // Fetch student's pending suggestion if status is reviewed
        Appointment suggestedAppointment = null;
        if ("reviewed".equals(report.getStatus())) {
            User student = report.getStudent();
            List<Appointment> suggestions = appointmentRepository.findByStudentAndStatus(student, AppointmentStatus.STUDENT_PROPOSED);
            if (!suggestions.isEmpty()) {
                suggestedAppointment = suggestions.get(0); // Get first pending suggestion
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("report", report);
        if (appointment != null) {
            model.addAttribute("appointment", appointment);
        }
        if (suggestedAppointment != null) {
            model.addAttribute("suggestedAppointment", suggestedAppointment);
        }
        model.addAttribute("page", "concerns/report-detail");
        model.addAttribute("title", "Report Details");
        model.addAttribute("activePage", "concerns");
        model.addAttribute("canEdit", isAuthenticatedStaff(user) || isAuthenticatedProfessional(user) || isAuthenticatedAdmin(user));

        return "layout";
    }

    /**
     * Get detailed report as JSON
     */
    @GetMapping("/concerns/api/{id}")
    @ResponseBody
    public ResponseEntity<?> getReportDetail(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Report report = reportRepository.findById(id).orElse(null);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        // Authorization: Check if user can view this report
        if (!canViewReport(user, report)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Map<String, Object> reportData = reportService.reportToMap(report);
        return ResponseEntity.ok(reportData);
    }

    // ==================== STAFF REPORT CREATION ====================
    /**
     * Display staff report creation page
     */
    @GetMapping("/concerns/staff-report")
    public String staffReportPage(HttpSession session, Model model, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");

        // Authorization: Only staff can create reports on behalf of students
        if (!isAuthenticatedStaff(user)) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("page", "concerns/staff-report");
        model.addAttribute("title", "Create Student Report");
        model.addAttribute("activePage", "concerns");

        return "layout";
    }

    /**
     * Submit a report on behalf of a student (staff only)
     */
    @PostMapping("/concerns/staff-submit")
    public String staffSubmitConcern(@RequestParam(required = false, name = "concerns") String[] concerns,
                                    @RequestParam String description,
                                    @RequestParam(required = false) String urgency,
                                    @RequestParam Long studentId,
                                    HttpSession session, HttpServletRequest request) {
        User staff = (User) session.getAttribute("user");

        // Authorization: Only staff can submit reports on behalf of students
        if (!isAuthenticatedStaff(staff)) {
            return "redirect:/login";
        }

        // Find the student
        User student = userRepository.findById(studentId).orElse(null);
        if (student == null || !UserRole.STUDENT.equals(student.getRole())) {
            return "redirect:/concerns/staff-report";
        }

        // Create report using service
        String type = (concerns != null && concerns.length > 0) 
            ? String.join(", ", concerns) 
            : "Other";

        reportService.createReport(student, type, description, urgency);

        // Redirect back to staff report page
        return "redirect:/concerns/staff-report";
    }

    // ==================== STAFF/PROFESSIONAL REPORT MANAGEMENT ====================
    /**
     * Get all students (for staff/professional dropdown)
     */
    @GetMapping("/api/students")
    @ResponseBody
    public ResponseEntity<?> getStudents(HttpSession session) {
        User user = (User) session.getAttribute("user");

        // Authorization: Only staff, professionals, and admins can view student list
        if (!isAuthenticatedStaff(user) && !isAuthenticatedProfessional(user) && !isAuthenticatedAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(List.of());
        }

        List<User> students = userRepository.findAll().stream()
                .filter(u -> UserRole.STUDENT.equals(u.getRole()))
                .collect(Collectors.toList());

        List<Map<String, Object>> studentData = students.stream()
                .map(s -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", s.getId());
                    map.put("name", s.getName());
                    map.put("email", s.getEmail());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(studentData);
    }

    /**
     * Update report status (staff/professional only)
     */
    @PostMapping("/concerns/api/{id}/status")
    @ResponseBody
    public ResponseEntity<?> updateReportStatus(@PathVariable Long id,
                                               @RequestParam String status,
                                               HttpSession session) {
        User user = (User) session.getAttribute("user");

        // Authorization: Only staff, professionals, and admins can update reports
        if (!isAuthenticatedStaff(user) && !isAuthenticatedProfessional(user) && !isAuthenticatedAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Report updatedReport = reportService.updateReportStatus(id, status);
        if (updatedReport == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> reportData = reportService.reportToMap(updatedReport);
        return ResponseEntity.ok(reportData);
    }

    /**
     * Resolve a report with notes (staff/professional only)
     */
    @PostMapping("/concerns/api/{id}/resolve")
    @ResponseBody
    public ResponseEntity<?> resolveReport(@PathVariable Long id,
                                          @RequestParam String resolutionNotes,
                                          HttpSession session) {
        User user = (User) session.getAttribute("user");

        // Authorization: Only staff, professionals, and admins can resolve reports
        if (!isAuthenticatedStaff(user) && !isAuthenticatedProfessional(user) && !isAuthenticatedAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Report resolvedReport = reportService.resolveReport(id, resolutionNotes);
        if (resolvedReport == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> reportData = reportService.reportToMap(resolvedReport);
        return ResponseEntity.ok(reportData);
    }

    /**
     * Get open reports for staff/professional dashboard
     */
    @GetMapping("/concerns/api/open-reports")
    @ResponseBody
    public ResponseEntity<?> getOpenReports(HttpSession session) {
        User user = (User) session.getAttribute("user");

        // Authorization: Only staff, professionals, and admins can view open reports
        if (!isAuthenticatedStaff(user) && !isAuthenticatedProfessional(user) && !isAuthenticatedAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(List.of());
        }

        List<Report> openReports = reportService.getOpenReports();
        List<Map<String, Object>> reportData = reportService.reportsToMapList(openReports);
        return ResponseEntity.ok(reportData);
    }

    /**
     * Get reports by status filter
     */
    @GetMapping("/concerns/api/by-status")
    @ResponseBody
    public ResponseEntity<?> getReportsByStatus(@RequestParam String status, HttpSession session) {
        User user = (User) session.getAttribute("user");

        // Authorization: Only staff, professionals, and admins can filter reports
        if (!isAuthenticatedStaff(user) && !isAuthenticatedProfessional(user) && !isAuthenticatedAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(List.of());
        }

        List<Report> reports = reportService.getReportsByStatus(status);
        List<Map<String, Object>> reportData = reportService.reportsToMapList(reports);
        return ResponseEntity.ok(reportData);
    }

    /**
     * Get reports by urgency filter
     */
    @GetMapping("/concerns/api/by-urgency")
    @ResponseBody
    public ResponseEntity<?> getReportsByUrgency(@RequestParam String urgency, HttpSession session) {
        User user = (User) session.getAttribute("user");

        // Authorization: Only staff, professionals, and admins can filter reports
        if (!isAuthenticatedStaff(user) && !isAuthenticatedProfessional(user) && !isAuthenticatedAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(List.of());
        }

        List<Report> reports = reportService.getReportsByUrgency(urgency);
        List<Map<String, Object>> reportData = reportService.reportsToMapList(reports);
        return ResponseEntity.ok(reportData);
    }

    /**
     * Get report history (resolved reports)
     */
    @GetMapping("/concerns/api/history")
    @ResponseBody
    public ResponseEntity<?> getReportHistory(@RequestParam(required = false) Long studentId, 
                                             HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(List.of());
        }

        List<Report> historyReports;

        if (studentId != null) {
            // Authorization: Only staff, professionals, and admins can view other student history
            if (!isAuthenticatedStaff(user) && !isAuthenticatedProfessional(user) && !isAuthenticatedAdmin(user)) {
                // Students can only view their own history
                if (!studentId.equals(user.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(List.of());
                }
            }
            historyReports = reportService.getStudentReportHistory(studentId);
        } else {
            // If no studentId provided, use current user's ID
            historyReports = reportService.getStudentReportHistory(user.getId());
        }

        List<Map<String, Object>> reportData = reportService.reportsToMapList(historyReports);
        return ResponseEntity.ok(reportData);
    }

    /**
     * Get reports within a date range (for reporting)
     */
    @GetMapping("/concerns/api/reports-by-date")
    @ResponseBody
    public ResponseEntity<?> getReportsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long studentId,
            HttpSession session) {
        User user = (User) session.getAttribute("user");

        // Authorization: Check permissions based on role
        if (!isAuthenticatedStaff(user) && !isAuthenticatedProfessional(user) && !isAuthenticatedAdmin(user)) {
            // Non-staff users can only get their own date range data
            if (!isAuthenticatedStudent(user) || studentId == null || !studentId.equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(List.of());
            }
        }

        List<Report> reports;
        if (studentId != null) {
            reports = reportService.getStudentReportsInDateRange(studentId, startDate, endDate);
        } else {
            reports = reportService.getReportsInDateRange(startDate, endDate);
        }

        List<Map<String, Object>> reportData = reportService.reportsToMapList(reports);
        return ResponseEntity.ok(reportData);
    }

    // ==================== STAFF PATIENT REPORTS VIEW ====================
    /**
     * Display all reports for a specific student (staff and professional view)
     */
    @GetMapping("/staff/patients/{studentId}/reports")
    public String viewStudentReports(@PathVariable Long studentId, HttpSession session, Model model, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");

        // Authorization: Only staff and professionals can access this page
        if (user == null || (user.getRole() != UserRole.STAFF && user.getRole() != UserRole.PROFESSIONAL)) {
            return "redirect:/login";
        }

        // Find the student
        User student = userRepository.findById(studentId).orElse(null);
        if (student == null || !UserRole.STUDENT.equals(student.getRole())) {
            return "redirect:/students";
        }

        // Get all reports for the student
        List<Report> reports = reportRepository.findByStudentId(studentId);

        // Add to model
        model.addAttribute("student", student);
        model.addAttribute("reports", reports);
        model.addAttribute("page", "staff/patient-reports");

        return "layout";
    }

    /**
     * Display report details for staff and professionals
     */
    @GetMapping("/staff/reports/{id}")
    public String viewStaffReportDetail(@PathVariable Long id, HttpSession session, Model model, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");

        // Authorization: Only staff and professionals can access this page
        if (user == null || (user.getRole() != UserRole.STAFF && user.getRole() != UserRole.PROFESSIONAL)) {
            return "redirect:/login";
        }

        Report report = reportRepository.findById(id).orElse(null);
        if (report == null) {
            return "redirect:/404";
        }

        // Do NOT auto-update status when staff views report
        // Staff should manually change status as needed

        // Fetch appointment if report status is scheduled
        Appointment appointment = null;
        System.out.println("\n=== DEBUG: viewStaffReportDetail ===");
        System.out.println("Report ID: " + report.getId());
        System.out.println("Report Status: " + report.getStatus());
        if ("scheduled".equals(report.getStatus())) {
            appointment = appointmentRepository.findByReportIdWithProfessional(report.getId());
            System.out.println("Appointment found: " + (appointment != null ? appointment.getId() : "null"));
            if (appointment != null) {
                System.out.println("Appointment Date: " + appointment.getAppointmentDate());
                System.out.println("Appointment Time: " + appointment.getTimeSlotStart() + " - " + appointment.getTimeSlotEnd());
            }
        }
        
        // Fetch student's pending suggestion if status is reviewed
        Appointment suggestedAppointment = null;
        if ("reviewed".equals(report.getStatus())) {
            User student = report.getStudent();
            List<Appointment> suggestions = appointmentRepository.findByStudentAndStatus(student, AppointmentStatus.STUDENT_PROPOSED);
            if (!suggestions.isEmpty()) {
                suggestedAppointment = suggestions.get(0); // Get first pending suggestion
            }
        }
        System.out.println("===================================\n");

        model.addAttribute("user", user);
        model.addAttribute("report", report);
        model.addAttribute("student", report.getStudent());
        if (appointment != null) {
            model.addAttribute("appointment", appointment);
        }
        if (suggestedAppointment != null) {
            model.addAttribute("suggestedAppointment", suggestedAppointment);
        }
        model.addAttribute("page", "concerns/staff-report-detail");
        model.addAttribute("title", "Report Detail");
        return "layout";
    }
}


