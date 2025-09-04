package com.hospital.controller;

import com.hospital.model.User;
import com.hospital.service.UserService;
import com.hospital.service.PatientService;
import com.hospital.service.AppointmentService;
import com.hospital.service.MedicalRecordService;
import com.hospital.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private MedicalRecordService medicalRecordService;
    
    @Autowired
    private PrescriptionService prescriptionService;

    @GetMapping("/login")
    public String login(Model model) {
        // Add some debug info
        long totalUsers = userService.getTotalUsers();
        model.addAttribute("debugInfo", "Total users in database: " + totalUsers);
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String confirmPassword,
                              @RequestParam String role,
                              @RequestParam(required = false) String adminKey,
                              Model model) {
        try {
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match");
                return "signup";
            }
            
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            
            // Validate admin key if registering as admin
            if (userRole == User.Role.ADMIN) {
                if (adminKey == null || !userService.validateAdminKey(adminKey)) {
                    model.addAttribute("error", "Invalid admin key");
                    return "signup";
                }
            }
            
            userService.registerUser(firstName, lastName, email, password, userRole);
            model.addAttribute("success", "Registration successful! Please login.");
            return "login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    @PostMapping("/custom-login")
    public String customLogin(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String role,
                             @RequestParam(required = false) String adminKey,
                             Model model,
                             jakarta.servlet.http.HttpServletRequest request) {
        try {
            User user = userService.findByEmail(username);
            if (user == null) {
                model.addAttribute("error", "Invalid email or password");
                return "login";
            }
            
            // Validate password
            if (!userService.validatePassword(password, user.getPassword())) {
                model.addAttribute("error", "Invalid email or password");
                return "login";
            }
            
            // Check if user is trying to login as admin
            if ("admin".equals(role)) {
                if (user.getRole() != User.Role.ADMIN) {
                    model.addAttribute("error", "Access denied: Admin privileges required");
                    return "login";
                }
                
                if (adminKey == null || !userService.validateAdminKey(adminKey)) {
                    model.addAttribute("error", "Invalid admin access key");
                    return "login";
                }
            } else if ("doctor".equals(role)) {
                if (user.getRole() != User.Role.DOCTOR) {
                    model.addAttribute("error", "Access denied: Doctor privileges required");
                    return "login";
                }
            }
            
            // Create session attributes to track logged in user
            request.getSession().setAttribute("loggedInUser", user);
            request.getSession().setAttribute("userRole", user.getRole().toString());
            
            // Redirect to appropriate dashboard
            if (user.getRole() == User.Role.ADMIN) {
                return "redirect:/admin-dashboard";
            } else {
                return "redirect:/doctor-dashboard";
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "Login failed: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        model.addAttribute("userRole", role);
        
        if (role.equals("ROLE_ADMIN")) {
            return "admin-dashboard";
        } else {
            return "doctor-dashboard";
        }
    }
    
    @GetMapping("/admin-dashboard")
    public String adminDashboard(Model model, jakarta.servlet.http.HttpServletRequest request) {
        // Check if user is logged in
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        String userRole = (String) request.getSession().getAttribute("userRole");
        
        if (currentUser == null || !"ADMIN".equals(userRole)) {
            return "redirect:/login";
        }
        
        // Get all users for admin dashboard
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("totalUsers", userService.getTotalUsers());
        model.addAttribute("totalDoctors", userService.getTotalDoctors());
        model.addAttribute("totalAdmins", userService.getTotalAdmins());
        model.addAttribute("allUsers", userService.getAllUsers());
        return "admin-dashboard";
    }
    
    @GetMapping("/doctor-dashboard")
    public String doctorDashboard(Model model, jakarta.servlet.http.HttpServletRequest request) {
        // Check if user is logged in
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        String userRole = (String) request.getSession().getAttribute("userRole");
        
        if (currentUser == null || !"DOCTOR".equals(userRole)) {
            return "redirect:/login";
        }
        
        // Get real data from services
        long totalPatients = patientService.countByDoctor(currentUser);
        long todayAppointments = appointmentService.countByDoctorAndDate(currentUser, LocalDate.now());
        long completedConsultations = appointmentService.countByDoctorAndStatus(currentUser, com.hospital.model.Appointment.Status.COMPLETED);
        long totalMedicalRecords = medicalRecordService.countByDoctor(currentUser);
        long activePrescriptions = prescriptionService.countByDoctorAndStatus(currentUser, com.hospital.model.Prescription.PrescriptionStatus.ACTIVE);
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("totalPatients", totalPatients);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("completedConsultations", completedConsultations);
        model.addAttribute("totalMedicalRecords", totalMedicalRecords);
        model.addAttribute("activePrescriptions", activePrescriptions);
        model.addAttribute("doctorPatients", patientService.findByDoctor(currentUser));
        model.addAttribute("todayAppointmentsList", appointmentService.findByDoctorAndDate(currentUser, LocalDate.now()));
        return "doctor-dashboard";
    }
    
    @GetMapping("/logout")
    public String logout(jakarta.servlet.http.HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login";
    }
}