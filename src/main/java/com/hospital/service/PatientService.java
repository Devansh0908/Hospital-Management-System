package com.hospital.service;

import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public Patient savePatient(Patient patient) {
        if (patient.getPatientId() == null || patient.getPatientId().isEmpty()) {
            patient.setPatientId(generatePatientId());
        }
        return patientRepository.save(patient);
    }

    public Patient findById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }
    
    public Patient findByPatientId(String patientId) {
        return patientRepository.findByPatientId(patientId).orElse(null);
    }

    public List<Patient> findByDoctor(User doctor) {
        return patientRepository.findByDoctor(doctor);
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }
    
    public List<Patient> findAllOrderByName() {
        return patientRepository.findAll().stream()
                .sorted((p1, p2) -> p1.getLastName().compareToIgnoreCase(p2.getLastName()))
                .toList();
    }

    public long countByDoctor(User doctor) {
        return patientRepository.countByDoctor(doctor);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
    
    // Search and Filter Methods
    public List<Patient> searchPatients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAllOrderByName();
        }
        return patientRepository.searchPatients(searchTerm.trim());
    }
    
    public List<Patient> findPatientsWithFilters(String searchTerm, Patient.PatientStatus status, 
                                               Patient.Gender gender, Patient.BloodGroup bloodGroup, User doctor) {
        return patientRepository.findPatientsWithFilters(searchTerm, status, gender, bloodGroup, doctor);
    }
    
    public List<Patient> findByStatus(Patient.PatientStatus status) {
        return patientRepository.findByStatusOrderByLastNameAsc(status);
    }
    
    public List<Patient> findByGender(Patient.Gender gender) {
        return patientRepository.findByGender(gender);
    }
    
    public List<Patient> findByBloodGroup(Patient.BloodGroup bloodGroup) {
        return patientRepository.findByBloodGroup(bloodGroup);
    }
    
    // Statistics Methods
    public Map<String, Object> getPatientStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalPatients", patientRepository.count());
        stats.put("activePatients", patientRepository.countByStatus(Patient.PatientStatus.ACTIVE));
        stats.put("inactivePatients", patientRepository.countByStatus(Patient.PatientStatus.INACTIVE));
        stats.put("dischargedPatients", patientRepository.countByStatus(Patient.PatientStatus.DISCHARGED));
        
        stats.put("malePatients", patientRepository.countByGender(Patient.Gender.MALE));
        stats.put("femalePatients", patientRepository.countByGender(Patient.Gender.FEMALE));
        
        LocalDate today = LocalDate.now();
        stats.put("newPatientsToday", patientRepository.countNewPatientsFromDate(today));
        stats.put("newPatientsThisWeek", patientRepository.countNewPatientsFromDate(today.minusDays(7)));
        stats.put("newPatientsThisMonth", patientRepository.countNewPatientsFromDate(today.minusDays(30)));
        
        return stats;
    }
    
    // Validation Methods
    public boolean existsByEmail(String email) {
        return patientRepository.existsByEmail(email);
    }
    
    public boolean existsByPatientId(String patientId) {
        return patientRepository.existsByPatientId(patientId);
    }
    
    // Patient ID Generation
    private String generatePatientId() {
        long count = patientRepository.count();
        String patientId;
        do {
            count++;
            patientId = String.format("P%04d", count);
        } while (existsByPatientId(patientId));
        
        return patientId;
    }
    
    // Update patient's last visit
    public void updateLastVisit(Long patientId) {
        Patient patient = findById(patientId);
        if (patient != null) {
            patient.setLastVisit(LocalDateTime.now());
            savePatient(patient);
        }
    }
    
    // Get patients by age range
    public List<Patient> getPatientsByAgeRange(int minAge, int maxAge) {
        LocalDate maxBirthDate = LocalDate.now().minusYears(minAge);
        LocalDate minBirthDate = LocalDate.now().minusYears(maxAge + 1);
        
        return patientRepository.findAll().stream()
                .filter(p -> p.getDateOfBirth() != null)
                .filter(p -> p.getDateOfBirth().isAfter(minBirthDate) && p.getDateOfBirth().isBefore(maxBirthDate))
                .toList();
    }
}