package com.hospital.controller;

import com.hospital.model.User;
import com.hospital.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private ExportService exportService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @Autowired
    private MedicalRecordService medicalRecordService;
    
    @Autowired
    private SystemReportService systemReportService;
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private AppointmentService appointmentService;

    // User Exports
    @GetMapping("/users/pdf")
    public ResponseEntity<byte[]> exportUsersPdf(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] pdfBytes = exportService.exportUsersToPdf(userService.getAllUsers());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "users_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
            
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users/excel")
    public ResponseEntity<byte[]> exportUsersExcel(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] excelBytes = exportService.exportUsersToExcel(userService.getAllUsers());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "users_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
            
            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users/csv")
    public ResponseEntity<String> exportUsersCSV(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String csvData = exportService.exportUsersToCSV(userService.getAllUsers());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", "users_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            
            return ResponseEntity.ok().headers(headers).body(csvData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Prescription Exports
    @GetMapping("/prescriptions/pdf")
    public ResponseEntity<byte[]> exportPrescriptionsPdf(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] pdfBytes = exportService.exportPrescriptionsToPdf(prescriptionService.findByDoctor(currentUser));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "prescriptions_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
            
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/prescriptions/excel")
    public ResponseEntity<byte[]> exportPrescriptionsExcel(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] excelBytes = exportService.exportPrescriptionsToExcel(prescriptionService.findByDoctor(currentUser));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "prescriptions_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
            
            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/prescriptions/csv")
    public ResponseEntity<String> exportPrescriptionsCSV(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String csvData = exportService.exportPrescriptionsToCSV(prescriptionService.findByDoctor(currentUser));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", "prescriptions_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            
            return ResponseEntity.ok().headers(headers).body(csvData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Medical Records Exports
    @GetMapping("/medical-records/pdf")
    public ResponseEntity<byte[]> exportMedicalRecordsPdf(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] pdfBytes = exportService.exportMedicalRecordsToPdf(medicalRecordService.findByDoctor(currentUser));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "medical_records_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
            
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/medical-records/excel")
    public ResponseEntity<byte[]> exportMedicalRecordsExcel(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] excelBytes = exportService.exportMedicalRecordsToExcel(medicalRecordService.findByDoctor(currentUser));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "medical_records_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
            
            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/medical-records/csv")
    public ResponseEntity<String> exportMedicalRecordsCSV(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.DOCTOR) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String csvData = exportService.exportMedicalRecordsToCSV(medicalRecordService.findByDoctor(currentUser));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", "medical_records_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            
            return ResponseEntity.ok().headers(headers).body(csvData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Room Exports (Admin only)
    @GetMapping("/rooms/pdf")
    public ResponseEntity<byte[]> exportRoomsPdf(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] pdfBytes = exportService.exportRoomsToPdf(roomService.findAll());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "rooms_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
            
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rooms/excel")
    public ResponseEntity<byte[]> exportRoomsExcel(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] excelBytes = exportService.exportRoomsToExcel(roomService.findAll());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "rooms_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
            
            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rooms/csv")
    public ResponseEntity<String> exportRoomsCSV(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String csvData = exportService.exportRoomsToCSV(roomService.findAll());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", "rooms_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            
            return ResponseEntity.ok().headers(headers).body(csvData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Department Exports (Admin only)
    @GetMapping("/departments/pdf")
    public ResponseEntity<byte[]> exportDepartmentsPdf(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] pdfBytes = exportService.exportDepartmentsToPdf(departmentService.findAll());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "departments_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
            
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/departments/excel")
    public ResponseEntity<byte[]> exportDepartmentsExcel(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] excelBytes = exportService.exportDepartmentsToExcel(departmentService.findAll());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "departments_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
            
            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/departments/csv")
    public ResponseEntity<String> exportDepartmentsCSV(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String csvData = exportService.exportDepartmentsToCSV(departmentService.findAll());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", "departments_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            
            return ResponseEntity.ok().headers(headers).body(csvData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Patient Exports (Admin and Doctor)
    @GetMapping("/patients/pdf")
    public ResponseEntity<byte[]> exportPatientsPdf(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || (currentUser.getRole() != User.Role.ADMIN && currentUser.getRole() != User.Role.DOCTOR)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] pdfBytes = exportService.exportPatientsToPdf(patientService.findAll());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "patients_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
            
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/patients/excel")
    public ResponseEntity<byte[]> exportPatientsExcel(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || (currentUser.getRole() != User.Role.ADMIN && currentUser.getRole() != User.Role.DOCTOR)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] excelBytes = exportService.exportPatientsToExcel(patientService.findAll());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "patients_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
            
            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/patients/csv")
    public ResponseEntity<String> exportPatientsCSV(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || (currentUser.getRole() != User.Role.ADMIN && currentUser.getRole() != User.Role.DOCTOR)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String csvData = exportService.exportPatientsToCSV(patientService.findAll());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", "patients_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            
            return ResponseEntity.ok().headers(headers).body(csvData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Appointment Exports (Admin and Doctor)
    @GetMapping("/appointments/pdf")
    public ResponseEntity<byte[]> exportAppointmentsPdf(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || (currentUser.getRole() != User.Role.ADMIN && currentUser.getRole() != User.Role.DOCTOR)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            var appointments = currentUser.getRole() == User.Role.ADMIN ? 
                appointmentService.findAll() : 
                appointmentService.findByDoctor(currentUser);
            
            byte[] pdfBytes = exportService.exportAppointmentsToPdf(appointments);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "appointments_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
            
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/appointments/excel")
    public ResponseEntity<byte[]> exportAppointmentsExcel(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || (currentUser.getRole() != User.Role.ADMIN && currentUser.getRole() != User.Role.DOCTOR)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            var appointments = currentUser.getRole() == User.Role.ADMIN ? 
                appointmentService.findAll() : 
                appointmentService.findByDoctor(currentUser);
            
            byte[] excelBytes = exportService.exportAppointmentsToExcel(appointments);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "appointments_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
            
            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/appointments/csv")
    public ResponseEntity<String> exportAppointmentsCSV(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || (currentUser.getRole() != User.Role.ADMIN && currentUser.getRole() != User.Role.DOCTOR)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            var appointments = currentUser.getRole() == User.Role.ADMIN ? 
                appointmentService.findAll() : 
                appointmentService.findByDoctor(currentUser);
            
            String csvData = exportService.exportAppointmentsToCSV(appointments);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", "appointments_report_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            
            return ResponseEntity.ok().headers(headers).body(csvData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // System Reports Exports (Admin only)
    @GetMapping("/system-report/pdf")
    public ResponseEntity<byte[]> exportSystemReportPdf(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            var systemOverview = systemReportService.generateSystemOverviewReport();
            var departmentReport = systemReportService.generateDepartmentReport();
            var roomUtilization = systemReportService.generateRoomUtilizationReport();
            var userActivity = systemReportService.generateUserActivityReport();
            var appointmentReport = systemReportService.generateAppointmentReport();
            
            var reportData = java.util.Map.of(
                "systemOverview", systemOverview,
                "departmentReport", departmentReport,
                "roomUtilization", roomUtilization,
                "userActivity", userActivity,
                "appointmentReport", appointmentReport
            );
            
            // PDF export for system reports not yet implemented
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("PDF export for system reports not yet implemented. Please use the web interface.".getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}