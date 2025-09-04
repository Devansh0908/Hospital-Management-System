package com.hospital.service;

import com.hospital.model.Prescription;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    public Prescription savePrescription(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }

    public Prescription findById(Long id) {
        return prescriptionRepository.findById(id).orElse(null);
    }

    public List<Prescription> findByPatient(Patient patient) {
        return prescriptionRepository.findByPatientOrderByPrescriptionDateDesc(patient);
    }

    public List<Prescription> findByDoctor(User doctor) {
        return prescriptionRepository.findByDoctorOrderByPrescriptionDateDesc(doctor);
    }

    public List<Prescription> findByPatientAndDoctor(Patient patient, User doctor) {
        return prescriptionRepository.findByPatientAndDoctorOrderByPrescriptionDateDesc(patient, doctor);
    }

    public List<Prescription> findByStatus(Prescription.PrescriptionStatus status) {
        return prescriptionRepository.findByStatusOrderByPrescriptionDateDesc(status);
    }

    public long countByDoctor(User doctor) {
        return prescriptionRepository.countByDoctor(doctor);
    }

    public long countByPatient(Patient patient) {
        return prescriptionRepository.countByPatient(patient);
    }

    public long countByDoctorAndStatus(User doctor, Prescription.PrescriptionStatus status) {
        return prescriptionRepository.countByDoctorAndStatus(doctor, status);
    }

    public void deletePrescription(Long id) {
        prescriptionRepository.deleteById(id);
    }
}