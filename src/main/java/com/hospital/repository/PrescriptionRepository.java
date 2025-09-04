package com.hospital.repository;

import com.hospital.model.Prescription;
import com.hospital.model.Patient;
import com.hospital.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatientOrderByPrescriptionDateDesc(Patient patient);
    List<Prescription> findByDoctorOrderByPrescriptionDateDesc(User doctor);
    List<Prescription> findByPatientAndDoctorOrderByPrescriptionDateDesc(Patient patient, User doctor);
    List<Prescription> findByStatusOrderByPrescriptionDateDesc(Prescription.PrescriptionStatus status);
    long countByDoctor(User doctor);
    long countByPatient(Patient patient);
    long countByDoctorAndStatus(User doctor, Prescription.PrescriptionStatus status);
}