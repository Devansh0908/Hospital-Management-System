package com.hospital.service;

import com.hospital.model.Appointment;
import com.hospital.model.User;
import com.hospital.model.Patient;
import com.hospital.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public Appointment findById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    public List<Appointment> findByDoctor(User doctor) {
        return appointmentRepository.findByDoctor(doctor);
    }

    public List<Appointment> findByPatient(Patient patient) {
        return appointmentRepository.findByPatient(patient);
    }

    public List<Appointment> findByDoctorAndDate(User doctor, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return appointmentRepository.findByDoctorAndAppointmentDateTimeBetween(doctor, startOfDay, endOfDay);
    }

    public long countByDoctorAndDate(User doctor, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return appointmentRepository.countByDoctorAndAppointmentDateTimeBetween(doctor, startOfDay, endOfDay);
    }

    public long countByDoctorAndStatus(User doctor, Appointment.Status status) {
        return appointmentRepository.countByDoctorAndStatus(doctor, status);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }
    
    // Methods for system reports
    public List<Appointment> findByStatus(Appointment.Status status) {
        return appointmentRepository.findByStatus(status);
    }
    
    public long countByStatus(Appointment.Status status) {
        return appointmentRepository.countByStatus(status);
    }
    
    public long countByDateRange(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.countByAppointmentDateTimeBetween(start, end);
    }
    
    public List<Appointment> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByAppointmentDateTimeBetween(start, end);
    }
    
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }
}