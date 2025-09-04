package com.hospital.service;

import com.hospital.model.MedicalRecord;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public MedicalRecord saveMedicalRecord(MedicalRecord medicalRecord) {
        return medicalRecordRepository.save(medicalRecord);
    }

    public MedicalRecord findById(Long id) {
        return medicalRecordRepository.findById(id).orElse(null);
    }

    public List<MedicalRecord> findByPatient(Patient patient) {
        return medicalRecordRepository.findByPatientOrderByRecordDateDesc(patient);
    }

    public List<MedicalRecord> findByDoctor(User doctor) {
        return medicalRecordRepository.findByDoctorOrderByRecordDateDesc(doctor);
    }

    public List<MedicalRecord> findByPatientAndDoctor(Patient patient, User doctor) {
        return medicalRecordRepository.findByPatientAndDoctorOrderByRecordDateDesc(patient, doctor);
    }

    public long countByDoctor(User doctor) {
        return medicalRecordRepository.countByDoctor(doctor);
    }

    public long countByPatient(Patient patient) {
        return medicalRecordRepository.countByPatient(patient);
    }

    public void deleteMedicalRecord(Long id) {
        medicalRecordRepository.deleteById(id);
    }
}