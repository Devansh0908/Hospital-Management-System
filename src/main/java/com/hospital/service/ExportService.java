package com.hospital.service;

import com.hospital.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

// iText PDF imports
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.colors.ColorConstants;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // PDF Export Methods - Basic implementation
    public byte[] exportUsersToPdf(List<User> users) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            document.add(new Paragraph("Hospital Management System - Users Report").setFontSize(18));
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DATETIME_FORMATTER)).setFontSize(10));
            document.add(new Paragraph(" "));

            // Add user data
            for (User user : users) {
                document.add(new Paragraph(
                    "Name: " + user.getFirstName() + " " + user.getLastName() + 
                    " | Email: " + user.getEmail() + 
                    " | Role: " + user.getRole() + 
                    " | Department: " + (user.getDepartment() != null ? user.getDepartment().getName() : "N/A")
                ).setFontSize(10));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Users: " + users.size()).setFontSize(12));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    public byte[] exportPrescriptionsToPdf(List<Prescription> prescriptions) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            document.add(new Paragraph("Hospital Management System - Prescriptions Report").setFontSize(18));
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DATETIME_FORMATTER)).setFontSize(10));
            document.add(new Paragraph(" "));

            // Add prescription data
            for (Prescription prescription : prescriptions) {
                document.add(new Paragraph(
                    "Patient: " + prescription.getPatient().getFirstName() + " " + prescription.getPatient().getLastName() + 
                    " | Doctor: " + prescription.getDoctor().getFirstName() + " " + prescription.getDoctor().getLastName() + 
                    " | Medication: " + prescription.getMedicationName() + 
                    " | Status: " + prescription.getStatus()
                ).setFontSize(10));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Prescriptions: " + prescriptions.size()).setFontSize(12));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    public byte[] exportMedicalRecordsToPdf(List<MedicalRecord> records) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            document.add(new Paragraph("Hospital Management System - Medical Records Report").setFontSize(18));
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DATETIME_FORMATTER)).setFontSize(10));
            document.add(new Paragraph(" "));

            // Add medical record data
            for (MedicalRecord record : records) {
                document.add(new Paragraph(
                    "Patient: " + record.getPatient().getFirstName() + " " + record.getPatient().getLastName() + 
                    " | Doctor: " + record.getDoctor().getFirstName() + " " + record.getDoctor().getLastName() + 
                    " | Date: " + record.getRecordDate().format(DATE_FORMATTER) + 
                    " | Type: " + record.getRecordType()
                ).setFontSize(10));
                
                if (record.getDiagnosis() != null) {
                    document.add(new Paragraph("  Diagnosis: " + record.getDiagnosis()).setFontSize(9));
                }
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Medical Records: " + records.size()).setFontSize(12));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    // Excel Export Methods
    public byte[] exportUsersToExcel(List<User> users) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Name", "Email", "Department", "Role", "Status", "Phone", "Specialization"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getFirstName() + " " + user.getLastName());
                row.createCell(1).setCellValue(user.getEmail());
                row.createCell(2).setCellValue(user.getDepartment() != null ? user.getDepartment().getName() : "N/A");
                row.createCell(3).setCellValue(user.getRole().toString());
                row.createCell(4).setCellValue(user.getStatus() != null ? user.getStatus().toString() : "ACTIVE");
                row.createCell(5).setCellValue(user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A");
                row.createCell(6).setCellValue(user.getSpecialization() != null ? user.getSpecialization() : "N/A");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }

    public byte[] exportPrescriptionsToExcel(List<Prescription> prescriptions) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Prescriptions");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Patient", "Doctor", "Medication", "Dosage", "Frequency", "Duration", "Status", "Date", "Instructions"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (Prescription prescription : prescriptions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(prescription.getPatient().getFirstName() + " " + prescription.getPatient().getLastName());
                row.createCell(1).setCellValue(prescription.getDoctor().getFirstName() + " " + prescription.getDoctor().getLastName());
                row.createCell(2).setCellValue(prescription.getMedicationName() + (prescription.getStrength() != null ? " - " + prescription.getStrength() : ""));
                row.createCell(3).setCellValue(prescription.getDosage());
                row.createCell(4).setCellValue(prescription.getFrequency());
                row.createCell(5).setCellValue(prescription.getDuration() + " days");
                row.createCell(6).setCellValue(prescription.getStatus().toString());
                row.createCell(7).setCellValue(prescription.getPrescriptionDate().format(DATETIME_FORMATTER));
                row.createCell(8).setCellValue(prescription.getInstructions() != null ? prescription.getInstructions() : "N/A");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }

    public byte[] exportMedicalRecordsToExcel(List<MedicalRecord> records) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Medical Records");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Patient", "Doctor", "Date", "Type", "Chief Complaint", "Diagnosis", "Treatment Plan", "Vital Signs"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (MedicalRecord record : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(record.getPatient().getFirstName() + " " + record.getPatient().getLastName());
                row.createCell(1).setCellValue(record.getDoctor().getFirstName() + " " + record.getDoctor().getLastName());
                row.createCell(2).setCellValue(record.getRecordDate().format(DATETIME_FORMATTER));
                row.createCell(3).setCellValue(record.getRecordType().toString());
                row.createCell(4).setCellValue(record.getChiefComplaint() != null ? record.getChiefComplaint() : "N/A");
                row.createCell(5).setCellValue(record.getDiagnosis() != null ? record.getDiagnosis() : "N/A");
                row.createCell(6).setCellValue(record.getTreatmentPlan() != null ? record.getTreatmentPlan() : "N/A");
                row.createCell(7).setCellValue(record.getVitalSigns() != null ? record.getVitalSigns() : "N/A");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }

    // CSV Export Methods
    public String exportUsersToCSV(List<User> users) {
        try (StringWriter stringWriter = new StringWriter(); CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            
            // Write header
            String[] headers = {"Name", "Email", "Department", "Role", "Status", "Phone", "Specialization"};
            csvWriter.writeNext(headers);

            // Write data
            for (User user : users) {
                String[] data = {
                    user.getFirstName() + " " + user.getLastName(),
                    user.getEmail(),
                    user.getDepartment() != null ? user.getDepartment().getName() : "N/A",
                    user.getRole().toString(),
                    user.getStatus() != null ? user.getStatus().toString() : "ACTIVE",
                    user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A",
                    user.getSpecialization() != null ? user.getSpecialization() : "N/A"
                };
                csvWriter.writeNext(data);
            }

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV", e);
        }
    }

    public String exportPrescriptionsToCSV(List<Prescription> prescriptions) {
        try (StringWriter stringWriter = new StringWriter(); CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            
            // Write header
            String[] headers = {"Patient", "Doctor", "Medication", "Dosage", "Frequency", "Duration", "Status", "Date", "Instructions"};
            csvWriter.writeNext(headers);

            // Write data
            for (Prescription prescription : prescriptions) {
                String[] data = {
                    prescription.getPatient().getFirstName() + " " + prescription.getPatient().getLastName(),
                    prescription.getDoctor().getFirstName() + " " + prescription.getDoctor().getLastName(),
                    prescription.getMedicationName() + (prescription.getStrength() != null ? " - " + prescription.getStrength() : ""),
                    prescription.getDosage(),
                    prescription.getFrequency(),
                    prescription.getDuration() + " days",
                    prescription.getStatus().toString(),
                    prescription.getPrescriptionDate().format(DATETIME_FORMATTER),
                    prescription.getInstructions() != null ? prescription.getInstructions() : "N/A"
                };
                csvWriter.writeNext(data);
            }

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV", e);
        }
    }

    public String exportMedicalRecordsToCSV(List<MedicalRecord> records) {
        try (StringWriter stringWriter = new StringWriter(); CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            
            // Write header
            String[] headers = {"Patient", "Doctor", "Date", "Type", "Chief Complaint", "Diagnosis", "Treatment Plan", "Vital Signs"};
            csvWriter.writeNext(headers);

            // Write data
            for (MedicalRecord record : records) {
                String[] data = {
                    record.getPatient().getFirstName() + " " + record.getPatient().getLastName(),
                    record.getDoctor().getFirstName() + " " + record.getDoctor().getLastName(),
                    record.getRecordDate().format(DATETIME_FORMATTER),
                    record.getRecordType().toString(),
                    record.getChiefComplaint() != null ? record.getChiefComplaint() : "N/A",
                    record.getDiagnosis() != null ? record.getDiagnosis() : "N/A",
                    record.getTreatmentPlan() != null ? record.getTreatmentPlan() : "N/A",
                    record.getVitalSigns() != null ? record.getVitalSigns() : "N/A"
                };
                csvWriter.writeNext(data);
            }

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV", e);
        }
    }

    // Room Export Methods
    public byte[] exportRoomsToExcel(List<Room> rooms) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Rooms");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Room Number", "Type", "Status", "Building", "Floor", "Capacity", "Department", "Daily Rate", "Description"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (Room room : rooms) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(room.getRoomNumber());
                row.createCell(1).setCellValue(room.getRoomType() != null ? room.getRoomType().toString() : "N/A");
                row.createCell(2).setCellValue(room.getStatus() != null ? room.getStatus().toString() : "N/A");
                row.createCell(3).setCellValue(room.getBuilding() != null ? room.getBuilding() : "N/A");
                row.createCell(4).setCellValue(room.getFloor() != null ? room.getFloor().toString() : "N/A");
                row.createCell(5).setCellValue(room.getCapacity() != null ? room.getCapacity().toString() : "N/A");
                row.createCell(6).setCellValue(room.getDepartment() != null ? room.getDepartment().getName() : "N/A");
                row.createCell(7).setCellValue(room.getDailyRate() != null ? "$" + room.getDailyRate().toString() : "N/A");
                row.createCell(8).setCellValue(room.getDescription() != null ? room.getDescription() : "N/A");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }

    public String exportRoomsToCSV(List<Room> rooms) {
        try (StringWriter stringWriter = new StringWriter(); CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            
            // Write header
            String[] headers = {"Room Number", "Type", "Status", "Building", "Floor", "Capacity", "Department", "Daily Rate", "Description"};
            csvWriter.writeNext(headers);

            // Write data
            for (Room room : rooms) {
                String[] data = {
                    room.getRoomNumber(),
                    room.getRoomType() != null ? room.getRoomType().toString() : "N/A",
                    room.getStatus() != null ? room.getStatus().toString() : "N/A",
                    room.getBuilding() != null ? room.getBuilding() : "N/A",
                    room.getFloor() != null ? room.getFloor().toString() : "N/A",
                    room.getCapacity() != null ? room.getCapacity().toString() : "N/A",
                    room.getDepartment() != null ? room.getDepartment().getName() : "N/A",
                    room.getDailyRate() != null ? "$" + room.getDailyRate().toString() : "N/A",
                    room.getDescription() != null ? room.getDescription() : "N/A"
                };
                csvWriter.writeNext(data);
            }

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV", e);
        }
    }

    // Department Export Methods
    public byte[] exportDepartmentsToExcel(List<Department> departments) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Departments");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Name", "Description", "Status", "Location", "Phone", "Email", "Capacity", "Specialization", "Head of Department"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (Department dept : departments) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(dept.getName());
                row.createCell(1).setCellValue(dept.getDescription() != null ? dept.getDescription() : "N/A");
                row.createCell(2).setCellValue(dept.getStatus() != null ? dept.getStatus().toString() : "ACTIVE");
                row.createCell(3).setCellValue(dept.getLocation() != null ? dept.getLocation() : "N/A");
                row.createCell(4).setCellValue(dept.getPhoneNumber() != null ? dept.getPhoneNumber() : "N/A");
                row.createCell(5).setCellValue(dept.getEmail() != null ? dept.getEmail() : "N/A");
                row.createCell(6).setCellValue(dept.getCapacity() != null ? dept.getCapacity().toString() : "N/A");
                row.createCell(7).setCellValue(dept.getSpecialization() != null ? dept.getSpecialization() : "N/A");
                row.createCell(8).setCellValue(dept.getHeadOfDepartment() != null ? 
                    "Dr. " + dept.getHeadOfDepartment().getFirstName() + " " + dept.getHeadOfDepartment().getLastName() : "N/A");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }

    public String exportDepartmentsToCSV(List<Department> departments) {
        try (StringWriter stringWriter = new StringWriter(); CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            
            // Write header
            String[] headers = {"Name", "Description", "Status", "Location", "Phone", "Email", "Capacity", "Specialization", "Head of Department"};
            csvWriter.writeNext(headers);

            // Write data
            for (Department dept : departments) {
                String[] data = {
                    dept.getName(),
                    dept.getDescription() != null ? dept.getDescription() : "N/A",
                    dept.getStatus() != null ? dept.getStatus().toString() : "ACTIVE",
                    dept.getLocation() != null ? dept.getLocation() : "N/A",
                    dept.getPhoneNumber() != null ? dept.getPhoneNumber() : "N/A",
                    dept.getEmail() != null ? dept.getEmail() : "N/A",
                    dept.getCapacity() != null ? dept.getCapacity().toString() : "N/A",
                    dept.getSpecialization() != null ? dept.getSpecialization() : "N/A",
                    dept.getHeadOfDepartment() != null ? 
                        "Dr. " + dept.getHeadOfDepartment().getFirstName() + " " + dept.getHeadOfDepartment().getLastName() : "N/A"
                };
                csvWriter.writeNext(data);
            }

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV", e);
        }
    }

    // Patient Export Methods
    public byte[] exportPatientsToExcel(List<Patient> patients) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Patients");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Name", "Date of Birth", "Gender", "Phone", "Email", "Address", "Emergency Contact", "Emergency Phone", "Medical History", "Allergies"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (Patient patient : patients) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(patient.getFirstName() + " " + patient.getLastName());
                row.createCell(1).setCellValue(patient.getDateOfBirth() != null ? patient.getDateOfBirth().format(DATE_FORMATTER) : "N/A");
                row.createCell(2).setCellValue(patient.getGender() != null ? patient.getGender().toString() : "N/A");
                row.createCell(3).setCellValue(patient.getPhone() != null ? patient.getPhone() : "N/A");
                row.createCell(4).setCellValue(patient.getEmail() != null ? patient.getEmail() : "N/A");
                row.createCell(5).setCellValue(patient.getAddress() != null ? patient.getAddress() : "N/A");
                row.createCell(6).setCellValue(patient.getEmergencyContact() != null ? patient.getEmergencyContact() : "N/A");
                row.createCell(7).setCellValue(patient.getEmergencyPhone() != null ? patient.getEmergencyPhone() : "N/A");
                row.createCell(8).setCellValue(patient.getMedicalHistory() != null ? patient.getMedicalHistory() : "N/A");
                row.createCell(9).setCellValue(patient.getAllergies() != null ? patient.getAllergies() : "N/A");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }

    public String exportPatientsToCSV(List<Patient> patients) {
        try (StringWriter stringWriter = new StringWriter(); CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            
            // Write header
            String[] headers = {"Name", "Date of Birth", "Gender", "Phone", "Email", "Address", "Emergency Contact", "Emergency Phone", "Medical History", "Allergies"};
            csvWriter.writeNext(headers);

            // Write data
            for (Patient patient : patients) {
                String[] data = {
                    patient.getFirstName() + " " + patient.getLastName(),
                    patient.getDateOfBirth() != null ? patient.getDateOfBirth().format(DATE_FORMATTER) : "N/A",
                    patient.getGender() != null ? patient.getGender().toString() : "N/A",
                    patient.getPhone() != null ? patient.getPhone() : "N/A",
                    patient.getEmail() != null ? patient.getEmail() : "N/A",
                    patient.getAddress() != null ? patient.getAddress() : "N/A",
                    patient.getEmergencyContact() != null ? patient.getEmergencyContact() : "N/A",
                    patient.getEmergencyPhone() != null ? patient.getEmergencyPhone() : "N/A",
                    patient.getMedicalHistory() != null ? patient.getMedicalHistory() : "N/A",
                    patient.getAllergies() != null ? patient.getAllergies() : "N/A"
                };
                csvWriter.writeNext(data);
            }

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV", e);
        }
    }

    // Appointment Export Methods
    public byte[] exportAppointmentsToExcel(List<Appointment> appointments) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Appointments");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Patient", "Doctor", "Date & Time", "Status", "Type", "Notes", "Symptoms", "Diagnosis", "Prescription"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (Appointment appointment : appointments) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName());
                row.createCell(1).setCellValue(appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());
                row.createCell(2).setCellValue(appointment.getAppointmentDateTime().format(DATETIME_FORMATTER));
                row.createCell(3).setCellValue(appointment.getStatus() != null ? appointment.getStatus().toString() : "N/A");
                row.createCell(4).setCellValue(appointment.getAppointmentType() != null ? appointment.getAppointmentType().toString() : "N/A");
                row.createCell(5).setCellValue(appointment.getNotes() != null ? appointment.getNotes() : "N/A");
                row.createCell(6).setCellValue(appointment.getSymptoms() != null ? appointment.getSymptoms() : "N/A");
                row.createCell(7).setCellValue(appointment.getDiagnosis() != null ? appointment.getDiagnosis() : "N/A");
                row.createCell(8).setCellValue(appointment.getPrescription() != null ? appointment.getPrescription() : "N/A");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }

    public String exportAppointmentsToCSV(List<Appointment> appointments) {
        try (StringWriter stringWriter = new StringWriter(); CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            
            // Write header
            String[] headers = {"Patient", "Doctor", "Date & Time", "Status", "Type", "Notes", "Symptoms", "Diagnosis", "Prescription"};
            csvWriter.writeNext(headers);

            // Write data
            for (Appointment appointment : appointments) {
                String[] data = {
                    appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName(),
                    appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName(),
                    appointment.getAppointmentDateTime().format(DATETIME_FORMATTER),
                    appointment.getStatus() != null ? appointment.getStatus().toString() : "N/A",
                    appointment.getAppointmentType() != null ? appointment.getAppointmentType().toString() : "N/A",
                    appointment.getNotes() != null ? appointment.getNotes() : "N/A",
                    appointment.getSymptoms() != null ? appointment.getSymptoms() : "N/A",
                    appointment.getDiagnosis() != null ? appointment.getDiagnosis() : "N/A",
                    appointment.getPrescription() != null ? appointment.getPrescription() : "N/A"
                };
                csvWriter.writeNext(data);
            }

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV", e);
        }
    }

    // Additional PDF Export Methods - Simplified implementation
    public byte[] exportRoomsToPdf(List<Room> rooms) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            document.add(new Paragraph("Hospital Management System - Rooms Report")
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

            // Add generation date
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DATETIME_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20));

            // Add room data as paragraphs
            for (Room room : rooms) {
                document.add(new Paragraph(
                    "Room: " + room.getRoomNumber() + 
                    " | Type: " + (room.getRoomType() != null ? room.getRoomType().toString() : "N/A") + 
                    " | Status: " + (room.getStatus() != null ? room.getStatus().toString() : "N/A") + 
                    " | Department: " + (room.getDepartment() != null ? room.getDepartment().getName() : "N/A")
                ).setFontSize(10).setMarginBottom(5));
            }

            // Add footer
            document.add(new Paragraph("Total Rooms: " + rooms.size())
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginTop(20));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    public byte[] exportDepartmentsToPdf(List<Department> departments) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            document.add(new Paragraph("Hospital Management System - Departments Report")
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

            // Add generation date
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DATETIME_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20));

            // Add department data as paragraphs
            for (Department dept : departments) {
                document.add(new Paragraph(
                    "Department: " + dept.getName() + 
                    " | Status: " + (dept.getStatus() != null ? dept.getStatus().toString() : "ACTIVE") + 
                    " | Location: " + (dept.getLocation() != null ? dept.getLocation() : "N/A") + 
                    " | Head: " + (dept.getHeadOfDepartment() != null ? 
                        "Dr. " + dept.getHeadOfDepartment().getFirstName() + " " + dept.getHeadOfDepartment().getLastName() : "N/A")
                ).setFontSize(10).setMarginBottom(5));
                
                if (dept.getDescription() != null) {
                    document.add(new Paragraph("Description: " + dept.getDescription())
                        .setFontSize(9).setMarginLeft(20).setMarginBottom(3));
                }
            }

            // Add footer
            document.add(new Paragraph("Total Departments: " + departments.size())
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginTop(20));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    public byte[] exportPatientsToPdf(List<Patient> patients) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            document.add(new Paragraph("Hospital Management System - Patients Report")
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

            // Add generation date
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DATETIME_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20));

            // Add patient data as paragraphs
            for (Patient patient : patients) {
                document.add(new Paragraph(
                    "Patient: " + patient.getFirstName() + " " + patient.getLastName() + 
                    " | DOB: " + (patient.getDateOfBirth() != null ? patient.getDateOfBirth().format(DATE_FORMATTER) : "N/A") + 
                    " | Gender: " + (patient.getGender() != null ? patient.getGender().toString() : "N/A") + 
                    " | Phone: " + (patient.getPhone() != null ? patient.getPhone() : "N/A")
                ).setFontSize(10).setMarginBottom(5));
                
                if (patient.getAllergies() != null) {
                    document.add(new Paragraph("Allergies: " + patient.getAllergies())
                        .setFontSize(9).setMarginLeft(20).setMarginBottom(3));
                }
            }

            // Add footer
            document.add(new Paragraph("Total Patients: " + patients.size())
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginTop(20));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    public byte[] exportAppointmentsToPdf(List<Appointment> appointments) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            document.add(new Paragraph("Hospital Management System - Appointments Report")
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

            // Add generation date
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DATETIME_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20));

            // Add appointment data as paragraphs
            for (Appointment appointment : appointments) {
                document.add(new Paragraph(
                    "Patient: " + appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName() + 
                    " | Doctor: " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName() + 
                    " | Date: " + appointment.getAppointmentDateTime().format(DATETIME_FORMATTER) + 
                    " | Status: " + (appointment.getStatus() != null ? appointment.getStatus().toString() : "N/A")
                ).setFontSize(10).setMarginBottom(5));
                
                if (appointment.getNotes() != null) {
                    document.add(new Paragraph("Notes: " + appointment.getNotes())
                        .setFontSize(9).setMarginLeft(20).setMarginBottom(3));
                }
            }

            // Add footer
            document.add(new Paragraph("Total Appointments: " + appointments.size())
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginTop(20));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}