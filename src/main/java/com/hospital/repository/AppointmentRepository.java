package com.hospital.repository;

import com.hospital.model.Appointment;
import com.hospital.model.Patient;
import com.hospital.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorOrderByAppointmentDateTimeAsc(User doctor);
    List<Appointment> findByDoctor(User doctor);
    List<Appointment> findByPatient(Patient patient);
    List<Appointment> findByDoctorAndAppointmentDateTimeBetween(User doctor, LocalDateTime start, LocalDateTime end);
    long countByDoctorAndAppointmentDateTimeBetween(User doctor, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDateTime BETWEEN :start AND :end ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findAppointmentsByDoctorAndDateRange(@Param("doctor") User doctor, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    long countByDoctorAndStatus(User doctor, Appointment.Status status);
    
    // Methods for system reports
    List<Appointment> findByStatus(Appointment.Status status);
    long countByStatus(Appointment.Status status);
    long countByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
}