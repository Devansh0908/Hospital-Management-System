package com.hospital.repository;

import com.hospital.model.Patient;
import com.hospital.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByDoctor(User doctor);
    List<Patient> findByDoctorOrderByLastNameAsc(User doctor);
    boolean existsByEmail(String email);
    boolean existsByPatientId(String patientId);
    long countByDoctor(User doctor);
    
    Optional<Patient> findByPatientId(String patientId);
    
    // Search functionality
    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.patientId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Patient> searchPatients(@Param("searchTerm") String searchTerm);
    
    // Filter by status
    List<Patient> findByStatus(Patient.PatientStatus status);
    List<Patient> findByStatusOrderByLastNameAsc(Patient.PatientStatus status);
    
    // Filter by gender
    List<Patient> findByGender(Patient.Gender gender);
    
    // Filter by blood group
    List<Patient> findByBloodGroup(Patient.BloodGroup bloodGroup);
    
    // Filter by date range
    List<Patient> findByRegistrationDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Advanced search with multiple criteria
    @Query("SELECT p FROM Patient p WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.patientId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:gender IS NULL OR p.gender = :gender) AND " +
           "(:bloodGroup IS NULL OR p.bloodGroup = :bloodGroup) AND " +
           "(:doctor IS NULL OR p.doctor = :doctor)")
    List<Patient> findPatientsWithFilters(
        @Param("searchTerm") String searchTerm,
        @Param("status") Patient.PatientStatus status,
        @Param("gender") Patient.Gender gender,
        @Param("bloodGroup") Patient.BloodGroup bloodGroup,
        @Param("doctor") User doctor
    );
    
    // Statistics
    long countByStatus(Patient.PatientStatus status);
    long countByGender(Patient.Gender gender);
    long countByBloodGroup(Patient.BloodGroup bloodGroup);
    
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.registrationDate >= :date")
    long countNewPatientsFromDate(@Param("date") LocalDate date);
}