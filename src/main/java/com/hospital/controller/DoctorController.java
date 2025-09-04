package com.hospital.controller;

import com.hospital.model.User;
import com.hospital.model.Patient;
import com.hospital.model.Appointment;
import com.hospital.model.MedicalRecord;
import com.hospital.model.Prescription;
import com.hospital.service.PatientService;
import com.hospital.service.AppointmentService;
import com.hospital.service.MedicalRecordService;
import com.hospital.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private PatientService patientService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private MedicalRecordService medicalRecordService;
    
    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping("/add-patient")
    public String addPatient(@RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String email,
                           @RequestParam String phone,
                           @RequestParam String dateOfBirth,
                           @RequestParam String gender,
                           @RequestParam String address,
                           @RequestParam(required = false) String medicalHistory,
                           @RequestParam(required = false) String allergies,
                           @RequestParam(required = false) String emergencyContact,
                           @RequestParam(required = false) String emergencyPhone,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) request.getSession().getAttribute("loggedInUser");
            if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
                return "redirect:/login";
            }

            Patient patient = new Patient();
            patient.setFirstName(firstName);
            patient.setLastName(lastName);
            patient.setEmail(email);
            patient.setPhone(phone);
            patient.setDateOfBirth(LocalDate.parse(dateOfBirth));
            patient.setGender(Patient.Gender.valueOf(gender.toUpperCase()));
            patient.setAddress(address);
            patient.setMedicalHistory(medicalHistory);
            patient.setAllergies(allergies);
            patient.setEmergencyContact(emergencyContact);
            patient.setEmergencyPhone(emergencyPhone);
            patient.setDoctor(currentUser);

            patientService.savePatient(patient);
            redirectAttributes.addFlashAttribute("success", "Patient added successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding patient: " + e.getMessage());
        }
        
        return "redirect:/doctor-dashboard";
    }

    @PostMapping("/schedule-appointment")
    public String scheduleAppointment(@RequestParam Long patientId,
                                    @RequestParam String appointmentDate,
                                    @RequestParam String appointmentTime,
                                    @RequestParam String appointmentType,
                                    @RequestParam(required = false) String notes,
                                    @RequestParam(required = false) String symptoms,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) request.getSession().getAttribute("loggedInUser");
            if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
                return "redirect:/login";
            }

            Patient patient = patientService.findById(patientId);
            if (patient == null) {
                redirectAttributes.addFlashAttribute("error", "Patient not found!");
                return "redirect:/doctor-dashboard";
            }

            Appointment appointment = new Appointment();
            appointment.setPatient(patient);
            appointment.setDoctor(currentUser);
            appointment.setAppointmentDateTime(LocalDateTime.parse(appointmentDate + "T" + appointmentTime));
            appointment.setAppointmentType(Appointment.AppointmentType.valueOf(appointmentType.toUpperCase()));
            appointment.setStatus(Appointment.Status.SCHEDULED);
            appointment.setNotes(notes);
            appointment.setSymptoms(symptoms);

            appointmentService.saveAppointment(appointment);
            redirectAttributes.addFlashAttribute("success", "Appointment scheduled successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error scheduling appointment: " + e.getMessage());
        }
        
        return "redirect:/doctor-dashboard";
    }

    @GetMapping("/patients")
    @ResponseBody
    public List<Patient> getPatients(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
            return List.of();
        }
        return patientService.findByDoctor(currentUser);
    }

    @PostMapping("/add-medical-record")
    public String addMedicalRecord(@RequestParam Long patientId,
                                 @RequestParam(required = false) Long appointmentId,
                                 @RequestParam String recordType,
                                 @RequestParam(required = false) String chiefComplaint,
                                 @RequestParam(required = false) String historyOfPresentIllness,
                                 @RequestParam(required = false) String physicalExamination,
                                 @RequestParam(required = false) String diagnosis,
                                 @RequestParam(required = false) String treatmentPlan,
                                 @RequestParam(required = false) String vitalSigns,
                                 @RequestParam(required = false) String allergies,
                                 @RequestParam(required = false) String medications,
                                 @RequestParam(required = false) String notes,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) request.getSession().getAttribute("loggedInUser");
            if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
                return "redirect:/login";
            }

            Patient patient = patientService.findById(patientId);
            if (patient == null) {
                redirectAttributes.addFlashAttribute("error", "Patient not found!");
                return "redirect:/doctor-dashboard";
            }

            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setPatient(patient);
            medicalRecord.setDoctor(currentUser);
            medicalRecord.setRecordDate(LocalDateTime.now());
            medicalRecord.setRecordType(MedicalRecord.RecordType.valueOf(recordType.toUpperCase()));
            medicalRecord.setChiefComplaint(chiefComplaint);
            medicalRecord.setHistoryOfPresentIllness(historyOfPresentIllness);
            medicalRecord.setPhysicalExamination(physicalExamination);
            medicalRecord.setDiagnosis(diagnosis);
            medicalRecord.setTreatmentPlan(treatmentPlan);
            medicalRecord.setVitalSigns(vitalSigns);
            medicalRecord.setAllergies(allergies);
            medicalRecord.setMedications(medications);
            medicalRecord.setNotes(notes);

            if (appointmentId != null) {
                Appointment appointment = appointmentService.findById(appointmentId);
                medicalRecord.setAppointment(appointment);
            }

            medicalRecordService.saveMedicalRecord(medicalRecord);
            redirectAttributes.addFlashAttribute("success", "Medical record added successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding medical record: " + e.getMessage());
        }
        
        return "redirect:/doctor-dashboard";
    }

    @PostMapping("/write-prescription")
    public String writePrescription(@RequestParam Long patientId,
                                  @RequestParam(required = false) Long appointmentId,
                                  @RequestParam String medicationName,
                                  @RequestParam String dosage,
                                  @RequestParam String frequency,
                                  @RequestParam Integer duration,
                                  @RequestParam String instructions,
                                  @RequestParam(required = false) String route,
                                  @RequestParam(required = false) String strength,
                                  @RequestParam(required = false) Integer quantity,
                                  @RequestParam(required = false) Integer refills,
                                  @RequestParam(required = false) String notes,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) request.getSession().getAttribute("loggedInUser");
            if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
                return "redirect:/login";
            }

            Patient patient = patientService.findById(patientId);
            if (patient == null) {
                redirectAttributes.addFlashAttribute("error", "Patient not found!");
                return "redirect:/doctor-dashboard";
            }

            Prescription prescription = new Prescription();
            prescription.setPatient(patient);
            prescription.setDoctor(currentUser);
            prescription.setPrescriptionDate(LocalDateTime.now());
            prescription.setMedicationName(medicationName);
            prescription.setDosage(dosage);
            prescription.setFrequency(frequency);
            prescription.setDuration(duration);
            prescription.setInstructions(instructions);
            prescription.setRoute(route);
            prescription.setStrength(strength);
            prescription.setQuantity(quantity);
            prescription.setRefills(refills);
            prescription.setStatus(Prescription.PrescriptionStatus.ACTIVE);
            prescription.setNotes(notes);
            prescription.setExpiryDate(LocalDate.now().plusDays(duration != null ? duration : 30));

            if (appointmentId != null) {
                Appointment appointment = appointmentService.findById(appointmentId);
                prescription.setAppointment(appointment);
            }

            prescriptionService.savePrescription(prescription);
            redirectAttributes.addFlashAttribute("success", "Prescription written successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error writing prescription: " + e.getMessage());
        }
        
        return "redirect:/doctor-dashboard";
    }

    @GetMapping("/medical-records/{patientId}")
    @ResponseBody
    public List<MedicalRecord> getPatientMedicalRecords(@PathVariable Long patientId, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
            return List.of();
        }
        
        Patient patient = patientService.findById(patientId);
        if (patient == null) {
            return List.of();
        }
        
        return medicalRecordService.findByPatientAndDoctor(patient, currentUser);
    }

    @GetMapping("/prescriptions/{patientId}")
    @ResponseBody
    public List<Prescription> getPatientPrescriptions(@PathVariable Long patientId, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
            return List.of();
        }
        
        Patient patient = patientService.findById(patientId);
        if (patient == null) {
            return List.of();
        }
        
        return prescriptionService.findByPatientAndDoctor(patient, currentUser);
    }

    @GetMapping("/view-patient-records")
    public String viewPatientRecords(Model model, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
            return "redirect:/login";
        }
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("doctorPatients", patientService.findByDoctor(currentUser));
        model.addAttribute("allMedicalRecords", medicalRecordService.findByDoctor(currentUser));
        return "view-patient-records";
    }

    @GetMapping("/view-prescriptions")
    public String viewPrescriptions(Model model, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
            return "redirect:/login";
        }
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("doctorPatients", patientService.findByDoctor(currentUser));
        model.addAttribute("allPrescriptions", prescriptionService.findByDoctor(currentUser));
        model.addAttribute("activePrescriptions", prescriptionService.findByStatus(Prescription.PrescriptionStatus.ACTIVE));
        return "view-prescriptions";
    }

    @PostMapping("/update-prescription-status")
    public String updatePrescriptionStatus(@RequestParam Long prescriptionId,
                                         @RequestParam String status,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) request.getSession().getAttribute("loggedInUser");
            if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
                return "redirect:/login";
            }

            Prescription prescription = prescriptionService.findById(prescriptionId);
            if (prescription == null || !prescription.getDoctor().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "Prescription not found or access denied!");
                return "redirect:/doctor/view-prescriptions";
            }

            prescription.setStatus(Prescription.PrescriptionStatus.valueOf(status.toUpperCase()));
            prescriptionService.savePrescription(prescription);
            redirectAttributes.addFlashAttribute("success", "Prescription status updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating prescription status: " + e.getMessage());
        }
        
        return "redirect:/doctor/view-prescriptions";
    }

    @GetMapping("/get-medical-record/{recordId}")
    @ResponseBody
    public java.util.Map<String, Object> getMedicalRecord(@PathVariable Long recordId, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
            return java.util.Map.of("error", "Unauthorized");
        }
        
        MedicalRecord record = medicalRecordService.findById(recordId);
        if (record == null || !record.getDoctor().getId().equals(currentUser.getId())) {
            return java.util.Map.of("error", "Record not found or access denied");
        }
        
        java.util.Map<String, Object> recordData = new java.util.HashMap<>();
        recordData.put("id", record.getId());
        recordData.put("patientName", record.getPatient().getFirstName() + " " + record.getPatient().getLastName());
        recordData.put("doctorName", "Dr. " + record.getDoctor().getFirstName() + " " + record.getDoctor().getLastName());
        recordData.put("recordDate", record.getRecordDate().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm")));
        recordData.put("recordType", record.getRecordType().toString());
        recordData.put("chiefComplaint", record.getChiefComplaint());
        recordData.put("historyOfPresentIllness", record.getHistoryOfPresentIllness());
        recordData.put("physicalExamination", record.getPhysicalExamination());
        recordData.put("diagnosis", record.getDiagnosis());
        recordData.put("treatmentPlan", record.getTreatmentPlan());
        recordData.put("vitalSigns", record.getVitalSigns());
        recordData.put("allergies", record.getAllergies());
        recordData.put("medications", record.getMedications());
        recordData.put("notes", record.getNotes());
        
        return recordData;
    }

    @PostMapping("/update-medical-record")
    public String updateMedicalRecord(@RequestParam Long recordId,
                                    @RequestParam String recordType,
                                    @RequestParam(required = false) String chiefComplaint,
                                    @RequestParam(required = false) String historyOfPresentIllness,
                                    @RequestParam(required = false) String physicalExamination,
                                    @RequestParam(required = false) String diagnosis,
                                    @RequestParam(required = false) String treatmentPlan,
                                    @RequestParam(required = false) String vitalSigns,
                                    @RequestParam(required = false) String allergies,
                                    @RequestParam(required = false) String medications,
                                    @RequestParam(required = false) String notes,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) request.getSession().getAttribute("loggedInUser");
            if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
                return "redirect:/login";
            }

            MedicalRecord record = medicalRecordService.findById(recordId);
            if (record == null || !record.getDoctor().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "Medical record not found or access denied!");
                return "redirect:/doctor/view-patient-records";
            }

            // Update the record fields
            record.setRecordType(MedicalRecord.RecordType.valueOf(recordType.toUpperCase()));
            record.setChiefComplaint(chiefComplaint);
            record.setHistoryOfPresentIllness(historyOfPresentIllness);
            record.setPhysicalExamination(physicalExamination);
            record.setDiagnosis(diagnosis);
            record.setTreatmentPlan(treatmentPlan);
            record.setVitalSigns(vitalSigns);
            record.setAllergies(allergies);
            record.setMedications(medications);
            record.setNotes(notes);

            medicalRecordService.saveMedicalRecord(record);
            redirectAttributes.addFlashAttribute("success", "Medical record updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating medical record: " + e.getMessage());
        }
        
        return "redirect:/doctor/view-patient-records";
    }
}