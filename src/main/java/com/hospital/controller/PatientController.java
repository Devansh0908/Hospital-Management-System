package com.hospital.controller;

import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.service.PatientService;
import com.hospital.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class PatientController {

    @Autowired
    private PatientService patientService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/patient-management")
    public String patientManagement(Model model, HttpServletRequest request,
                                  @RequestParam(required = false) String search,
                                  @RequestParam(required = false) String status,
                                  @RequestParam(required = false) String gender,
                                  @RequestParam(required = false) String bloodGroup,
                                  @RequestParam(required = false) Long doctorId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String userEmail = authentication.getName();
        User currentUser = userService.findByEmail(userEmail);
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }
        
        // Get filtered patients
        List<Patient> patients;
        if (search != null || status != null || gender != null || bloodGroup != null || doctorId != null) {
            Patient.PatientStatus patientStatus = status != null && !status.isEmpty() ? 
                Patient.PatientStatus.valueOf(status) : null;
            Patient.Gender patientGender = gender != null && !gender.isEmpty() ? 
                Patient.Gender.valueOf(gender) : null;
            Patient.BloodGroup patientBloodGroup = bloodGroup != null && !bloodGroup.isEmpty() ? 
                Patient.BloodGroup.valueOf(bloodGroup) : null;
            User doctor = doctorId != null ? userService.findById(doctorId) : null;
            
            patients = patientService.findPatientsWithFilters(search, patientStatus, patientGender, patientBloodGroup, doctor);
        } else {
            patients = patientService.findAllOrderByName();
        }
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allPatients", patients);
        model.addAttribute("patientStatistics", patientService.getPatientStatistics());
        model.addAttribute("allDoctors", userService.getAllDoctors());
        
        // Add filter values back to model for form persistence
        model.addAttribute("searchTerm", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedGender", gender);
        model.addAttribute("selectedBloodGroup", bloodGroup);
        model.addAttribute("selectedDoctorId", doctorId);
        
        return "patient-management";
    }

    @PostMapping("/create-patient")
    public String createPatient(@RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String email,
                              @RequestParam String phone,
                              @RequestParam String dateOfBirth,
                              @RequestParam String gender,
                              @RequestParam String address,
                              @RequestParam(required = false) String city,
                              @RequestParam(required = false) String state,
                              @RequestParam(required = false) String zipCode,
                              @RequestParam(required = false) String country,
                              @RequestParam(required = false) String nationality,
                              @RequestParam(required = false) String bloodGroup,
                              @RequestParam(required = false) String maritalStatus,
                              @RequestParam(required = false) String occupation,
                              @RequestParam(required = false) String medicalHistory,
                              @RequestParam(required = false) String allergies,
                              @RequestParam(required = false) String currentMedications,
                              @RequestParam(required = false) String emergencyContact,
                              @RequestParam(required = false) String emergencyPhone,
                              @RequestParam(required = false) String emergencyRelation,
                              @RequestParam(required = false) String insuranceProvider,
                              @RequestParam(required = false) String insurancePolicyNumber,
                              @RequestParam(required = false) String insuranceGroupNumber,
                              @RequestParam(required = false) Long doctorId,
                              @RequestParam(required = false) String notes,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/login";
            }
            
            String userEmail = authentication.getName();
            User currentUser = userService.findByEmail(userEmail);
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                return "redirect:/login";
            }

            // Check if email already exists
            if (patientService.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "A patient with this email already exists!");
                return "redirect:/admin/patient-management";
            }

            Patient patient = new Patient();
            patient.setFirstName(firstName);
            patient.setLastName(lastName);
            patient.setEmail(email);
            patient.setPhone(phone);
            patient.setDateOfBirth(LocalDate.parse(dateOfBirth));
            patient.setGender(Patient.Gender.valueOf(gender.toUpperCase()));
            patient.setAddress(address);
            patient.setCity(city);
            patient.setState(state);
            patient.setZipCode(zipCode);
            patient.setCountry(country);
            patient.setNationality(nationality);
            
            if (bloodGroup != null && !bloodGroup.isEmpty()) {
                patient.setBloodGroup(Patient.BloodGroup.valueOf(bloodGroup));
            }
            
            if (maritalStatus != null && !maritalStatus.isEmpty()) {
                patient.setMaritalStatus(Patient.MaritalStatus.valueOf(maritalStatus.toUpperCase()));
            }
            
            patient.setOccupation(occupation);
            patient.setMedicalHistory(medicalHistory);
            patient.setAllergies(allergies);
            patient.setCurrentMedications(currentMedications);
            patient.setEmergencyContact(emergencyContact);
            patient.setEmergencyPhone(emergencyPhone);
            patient.setEmergencyRelation(emergencyRelation);
            patient.setInsuranceProvider(insuranceProvider);
            patient.setInsurancePolicyNumber(insurancePolicyNumber);
            patient.setInsuranceGroupNumber(insuranceGroupNumber);
            patient.setNotes(notes);
            
            if (doctorId != null) {
                User doctor = userService.findById(doctorId);
                patient.setDoctor(doctor);
            }

            patientService.savePatient(patient);
            redirectAttributes.addFlashAttribute("success", "Patient registered successfully! Patient ID: " + patient.getPatientId());
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error registering patient: " + e.getMessage());
        }
        
        return "redirect:/admin/patient-management";
    }

    @PostMapping("/update-patient-status")
    public String updatePatientStatus(@RequestParam Long patientId,
                                    @RequestParam String status,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/login";
            }
            
            String userEmail = authentication.getName();
            User currentUser = userService.findByEmail(userEmail);
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                return "redirect:/login";
            }

            Patient patient = patientService.findById(patientId);
            if (patient == null) {
                redirectAttributes.addFlashAttribute("error", "Patient not found!");
                return "redirect:/admin/patient-management";
            }

            patient.setStatus(Patient.PatientStatus.valueOf(status.toUpperCase()));
            patientService.savePatient(patient);
            redirectAttributes.addFlashAttribute("success", "Patient status updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating patient status: " + e.getMessage());
        }
        
        return "redirect:/admin/patient-management";
    }

    @GetMapping("/patient-details/{id}")
    public String patientDetails(@PathVariable Long id, Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String userEmail = authentication.getName();
        User currentUser = userService.findByEmail(userEmail);
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }
        
        Patient patient = patientService.findById(id);
        if (patient == null) {
            return "redirect:/admin/patient-management";
        }
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("patient", patient);
        
        return "patient-details";
    }

    @PostMapping("/update-patient")
    public String updatePatient(@RequestParam Long patientId,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String email,
                              @RequestParam String phone,
                              @RequestParam String address,
                              @RequestParam(required = false) String city,
                              @RequestParam(required = false) String state,
                              @RequestParam(required = false) String zipCode,
                              @RequestParam(required = false) String medicalHistory,
                              @RequestParam(required = false) String allergies,
                              @RequestParam(required = false) String currentMedications,
                              @RequestParam(required = false) String emergencyContact,
                              @RequestParam(required = false) String emergencyPhone,
                              @RequestParam(required = false) String notes,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/login";
            }
            
            String userEmail = authentication.getName();
            User currentUser = userService.findByEmail(userEmail);
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                return "redirect:/login";
            }

            Patient patient = patientService.findById(patientId);
            if (patient == null) {
                redirectAttributes.addFlashAttribute("error", "Patient not found!");
                return "redirect:/admin/patient-management";
            }

            // Check if email is being changed and if it already exists
            if (!patient.getEmail().equals(email) && patientService.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "A patient with this email already exists!");
                return "redirect:/admin/patient-details/" + patientId;
            }

            patient.setFirstName(firstName);
            patient.setLastName(lastName);
            patient.setEmail(email);
            patient.setPhone(phone);
            patient.setAddress(address);
            patient.setCity(city);
            patient.setState(state);
            patient.setZipCode(zipCode);
            patient.setMedicalHistory(medicalHistory);
            patient.setAllergies(allergies);
            patient.setCurrentMedications(currentMedications);
            patient.setEmergencyContact(emergencyContact);
            patient.setEmergencyPhone(emergencyPhone);
            patient.setNotes(notes);

            patientService.savePatient(patient);
            redirectAttributes.addFlashAttribute("success", "Patient information updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating patient: " + e.getMessage());
        }
        
        return "redirect:/admin/patient-details/" + patientId;
    }

    @PostMapping("/delete-patient")
    public String deletePatient(@RequestParam Long patientId,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/login";
            }
            
            String userEmail = authentication.getName();
            User currentUser = userService.findByEmail(userEmail);
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                return "redirect:/login";
            }

            patientService.deletePatient(patientId);
            redirectAttributes.addFlashAttribute("success", "Patient deleted successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting patient: " + e.getMessage());
        }
        
        return "redirect:/admin/patient-management";
    }
}