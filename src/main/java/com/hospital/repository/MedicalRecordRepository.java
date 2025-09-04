package com.hospital.repository;

import com.hospital.model.MedicalRecord;
import com.hospital.model.Patient;
import com.hospital.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientOrderByRecordDateDesc(Patient patient);
    List<MedicalRecord> findByDoctorOrderByRecordDateDesc(User doctor);
    List<MedicalRecord> findByPatientAndDoctorOrderByRecordDateDesc(Patient patient, User doctor);
    long countByDoctor(User doctor);
    long countByPatient(Patient patient);
}