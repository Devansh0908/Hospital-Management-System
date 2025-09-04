package com.hospital.service;

import com.hospital.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemReportService {

    @Autowired
    private UserService userService;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private MedicalRecordService medicalRecordService;
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @Autowired
    private RoomService roomService;

    public Map<String, Object> generateSystemOverviewReport() {
        Map<String, Object> report = new HashMap<>();
        
        // User Statistics
        report.put("totalUsers", userService.getTotalUsers());
        report.put("totalDoctors", userService.getTotalDoctors());
        report.put("totalAdmins", userService.getTotalAdmins());
        
        // Department Statistics
        report.put("totalDepartments", departmentService.getTotalDepartments());
        report.put("activeDepartments", departmentService.getActiveDepartments());
        
        // Room Statistics
        report.put("totalRooms", roomService.getTotalRooms());
        report.put("availableRooms", roomService.countByStatus(Room.RoomStatus.AVAILABLE));
        report.put("occupiedRooms", roomService.countByStatus(Room.RoomStatus.OCCUPIED));
        report.put("maintenanceRooms", roomService.countByStatus(Room.RoomStatus.MAINTENANCE));
        
        // Patient Statistics
        report.put("totalPatients", patientService.findAll().size());
        
        // Appointment Statistics
        report.put("totalAppointments", appointmentService.findByStatus(Appointment.Status.SCHEDULED).size());
        report.put("todayAppointments", getTodayAppointmentsCount());
        
        // Medical Records Statistics
        report.put("totalMedicalRecords", getAllMedicalRecordsCount());
        
        // Prescription Statistics
        report.put("activePrescriptions", prescriptionService.findByStatus(Prescription.PrescriptionStatus.ACTIVE).size());
        
        return report;
    }

    public Map<String, Object> generateDepartmentReport() {
        Map<String, Object> report = new HashMap<>();
        List<Department> departments = departmentService.findAll();
        
        Map<String, Object> departmentStats = new HashMap<>();
        for (Department dept : departments) {
            Map<String, Object> deptData = new HashMap<>();
            deptData.put("name", dept.getName());
            deptData.put("status", dept.getStatus());
            deptData.put("doctorCount", userService.findByRole(User.Role.DOCTOR).stream()
                .filter(u -> u.getDepartment() != null && u.getDepartment().getId().equals(dept.getId()))
                .count());
            deptData.put("roomCount", roomService.countByDepartment(dept));
            deptData.put("headOfDepartment", dept.getHeadOfDepartment() != null ? 
                dept.getHeadOfDepartment().getFirstName() + " " + dept.getHeadOfDepartment().getLastName() : "Not Assigned");
            
            departmentStats.put(dept.getId().toString(), deptData);
        }
        
        report.put("departments", departmentStats);
        report.put("totalDepartments", departments.size());
        
        return report;
    }

    public Map<String, Object> generateRoomUtilizationReport() {
        Map<String, Object> report = new HashMap<>();
        
        // Room status distribution
        Map<String, Long> statusDistribution = new HashMap<>();
        for (Room.RoomStatus status : Room.RoomStatus.values()) {
            statusDistribution.put(status.name(), roomService.countByStatus(status));
        }
        report.put("statusDistribution", statusDistribution);
        
        // Room type distribution
        Map<String, Long> typeDistribution = new HashMap<>();
        for (Room.RoomType type : Room.RoomType.values()) {
            typeDistribution.put(type.name(), roomService.countByRoomType(type));
        }
        report.put("typeDistribution", typeDistribution);
        
        // Occupancy rate
        long totalRooms = roomService.getTotalRooms();
        long occupiedRooms = roomService.countByStatus(Room.RoomStatus.OCCUPIED);
        double occupancyRate = totalRooms > 0 ? (double) occupiedRooms / totalRooms * 100 : 0;
        report.put("occupancyRate", Math.round(occupancyRate * 100.0) / 100.0);
        
        report.put("totalRooms", totalRooms);
        report.put("occupiedRooms", occupiedRooms);
        report.put("availableRooms", roomService.countByStatus(Room.RoomStatus.AVAILABLE));
        
        return report;
    }

    public Map<String, Object> generateUserActivityReport() {
        Map<String, Object> report = new HashMap<>();
        
        List<User> allUsers = userService.getAllUsers();
        
        // User status distribution
        Map<String, Long> statusDistribution = new HashMap<>();
        statusDistribution.put("ACTIVE", allUsers.stream().filter(u -> u.getStatus() == User.UserStatus.ACTIVE).count());
        statusDistribution.put("INACTIVE", allUsers.stream().filter(u -> u.getStatus() == User.UserStatus.INACTIVE).count());
        statusDistribution.put("SUSPENDED", allUsers.stream().filter(u -> u.getStatus() == User.UserStatus.SUSPENDED).count());
        
        report.put("userStatusDistribution", statusDistribution);
        
        // Role distribution
        Map<String, Long> roleDistribution = new HashMap<>();
        roleDistribution.put("DOCTOR", userService.getTotalDoctors());
        roleDistribution.put("ADMIN", userService.getTotalAdmins());
        
        report.put("roleDistribution", roleDistribution);
        
        // Recent registrations (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long recentRegistrations = allUsers.stream()
            .filter(u -> u.getCreatedDate() != null && u.getCreatedDate().isAfter(thirtyDaysAgo))
            .count();
        
        report.put("recentRegistrations", recentRegistrations);
        report.put("totalUsers", allUsers.size());
        
        return report;
    }

    public Map<String, Object> generateAppointmentReport() {
        Map<String, Object> report = new HashMap<>();
        
        // This would typically get data from appointment service
        // For now, we'll create sample data structure
        report.put("todayAppointments", getTodayAppointmentsCount());
        report.put("weeklyAppointments", getWeeklyAppointmentsCount());
        report.put("monthlyAppointments", getMonthlyAppointmentsCount());
        
        // Appointment status distribution
        Map<String, Long> statusDistribution = new HashMap<>();
        for (Appointment.Status status : Appointment.Status.values()) {
            // This would use actual appointment service methods
            statusDistribution.put(status.name(), 0L);
        }
        report.put("appointmentStatusDistribution", statusDistribution);
        
        return report;
    }

    private long getTodayAppointmentsCount() {
        // This would typically use appointment service to get today's appointments
        return 0L; // Placeholder
    }

    private long getWeeklyAppointmentsCount() {
        // This would typically use appointment service to get this week's appointments
        return 0L; // Placeholder
    }

    private long getMonthlyAppointmentsCount() {
        // This would typically use appointment service to get this month's appointments
        return 0L; // Placeholder
    }

    private long getAllMedicalRecordsCount() {
        // This would typically use medical record service
        return 0L; // Placeholder
    }
}