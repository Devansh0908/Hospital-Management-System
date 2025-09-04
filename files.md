## Project Files Reference

This document catalogs the key files in the Hospital Management System, explains what each does, and shows how they interact. Use it alongside `README.md` and `WORKFLOW.md` for architecture and data flow details.

### Root
- `pom.xml`
  - Maven build descriptor. Declares Spring Boot, Spring Web, Spring Security, Spring Data JPA, H2, Thymeleaf and plugin versions. Controls packaging and run goals.
  - Flow/requirements: Required for dependency resolution and building the JAR. `mvn spring-boot:run` uses this.

- `README.md`
  - High-level overview, setup, run, routes, and troubleshooting.

- `WORKFLOW.md`
  - Detailed request lifecycle, auth, and per-feature data flow.

### Configuration
- `src/main/resources/application.properties`
  - Application settings: server port (8081), datasource (H2 file DB under `data/`), JPA settings, Thymeleaf config.
  - Flow: Loaded at startup; Spring Boot auto-config wires DataSource, JPA, Thymeleaf accordingly.

- `src/main/java/com/hospital/config/SecurityConfig.java`
  - Enables Spring Security; defines `SecurityFilterChain`, `PasswordEncoder`, authentication provider, and a custom login success handler.
  - Flow: On `POST /login`, resolves user via `CustomUserDetailsService`, sets `loggedInUser` + `userRole` in session, redirects ADMIN → `/admin-dashboard`, DOCTOR → `/doctor-dashboard`.
  - Requirements: BCrypt passwords; `CustomUserDetailsService` must retrieve users; `UserService.findByEmail` must return a `User` entity.

### Entry Point
- `src/main/java/com/hospital/HospitalManagementApplication.java`
  - Standard Spring Boot launcher (`main` method). Starts the embedded Tomcat, initializes components.

### Controllers (Web Layer)
- `src/main/java/com/hospital/controller/AuthController.java`
  - Renders login and signup pages; may handle signup creation logic (if enabled).
  - Flow: GET `/login` → `login.html`; `POST /signup` (optional) → `UserService` → repo.

- `src/main/java/com/hospital/controller/AdminController.java`
  - Serves admin dashboard and admin-only pages’ shells (navigation, summaries).
  - Flow: Validates admin role; prepares dashboard metrics via services; renders `admin-dashboard.html`.

- `src/main/java/com/hospital/controller/DoctorController.java`
  - Serves doctor dashboard and doctor-scoped operations (appointments, assigned patients).
  - Flow: Confirms doctor role; calls services to load doctor-specific data; renders `doctor-dashboard.html`.

- `src/main/java/com/hospital/controller/PatientController.java`
  - Full patient CRUD and filtering (ADMIN only).
  - Endpoints:
    - GET `/admin/patient-management` (filters: `search`, `status`, `gender`, `bloodGroup`, `doctorId`)
    - GET `/admin/patient-details/{id}`
    - POST `/admin/create-patient`, `/admin/update-patient`, `/admin/delete-patient`, `/admin/update-patient-status`
  - Flow: Reads `Authentication` → email → `UserService.findByEmail` → verify ADMIN. Delegates to `PatientService` and `UserService` (doctors list). Puts lists/metrics into `Model` → Thymeleaf.
  - Requirements: Valid enums for status/gender/blood group; duplicate email checks; date parsing; optional doctor association.

- `src/main/java/com/hospital/controller/ExportController.java`
  - Exposes export endpoints (CSV/Excel/PDF) for patients, appointments, reports.
  - Flow: Parses query params → `ExportService` for data assembly and streaming response. Sets content-disposition headers.
  - Requirements: `ExportService` must compose rows and handle formats; ensure only authorized roles access sensitive exports.

### Services (Business Layer)
- `src/main/java/com/hospital/service/CustomUserDetailsService.java`
  - Adapts `User` entities to Spring Security’s `UserDetails`. Used by the DaoAuthenticationProvider.
  - Flow: `loadUserByUsername(email)` → `UserRepository.findByEmail` → returns `UserDetails` with role.

- `src/main/java/com/hospital/service/UserService.java`
  - User operations: find by email/id, list doctors, create/update users.
  - Flow: Called by controllers and `SecurityConfig` success handler; delegates to `UserRepository`.

- `src/main/java/com/hospital/service/PatientService.java`
  - Patient CRUD and business logic (filters, statistics, validations).
  - Key methods: `findAllOrderByName`, `findPatientsWithFilters`, `existsByEmail`, `getPatientStatistics`, `savePatient`, `findById`, `deletePatient`.
  - Flow: Uses `PatientRepository` and occasionally `UserRepository` for doctor association.

- `src/main/java/com/hospital/service/AppointmentService.java`
  - Manage appointments: create, list (by doctor/patient), status updates, scheduling rules.
  - Flow: Coordinates with `UserRepository` (doctor) and `PatientRepository` (patient) as needed.

- `src/main/java/com/hospital/service/MedicalRecordService.java`
  - Create/list medical records tied to patients and possibly appointments.

- `src/main/java/com/hospital/service/PrescriptionService.java`
  - Create/list prescriptions; linked with patients and doctors.

- `src/main/java/com/hospital/service/DepartmentService.java`
  - CRUD for departments; used by admin management UI.

- `src/main/java/com/hospital/service/RoomService.java`
  - CRUD for rooms; supports patient room allocation features.

- `src/main/java/com/hospital/service/SystemReportService.java`
  - Aggregates analytics, summaries, and KPI-style reports for dashboards and exports.

- `src/main/java/com/hospital/service/DatabaseManagementService.java`
  - Admin-only utilities: backup/restore, purge, and maintenance operations.

- `src/main/java/com/hospital/service/SystemSettingsService.java`
  - Reads/writes system-wide configurable settings.

- `src/main/java/com/hospital/service/ExportService.java`
  - Generates tabular data for export, formats into CSV/Excel/PDF, and streams it back.
  - Flow: Pulls data through other services/repositories, transforms to rows, writes to response output streams.

### Repositories (Persistence Layer)
- `src/main/java/com/hospital/repository/UserRepository.java`
  - Spring Data JPA interface for `User` with `findByEmail`, `findByRole`, etc.

- `src/main/java/com/hospital/repository/PatientRepository.java`
  - Queries for patient filtering/sorting; uniqueness by email; CRUD.

- `src/main/java/com/hospital/repository/DepartmentRepository.java`
  - Department entity CRUD.

- `src/main/java/com/hospital/repository/RoomRepository.java`
  - Room entity CRUD.

- `src/main/java/com/hospital/repository/AppointmentRepository.java`
  - Appointment entity CRUD and finder methods (by doctor, by patient, by date/time).

- `src/main/java/com/hospital/repository/MedicalRecordRepository.java`
  - MedicalRecord entity CRUD; find by patient.

- `src/main/java/com/hospital/repository/PrescriptionRepository.java`
  - Prescription entity CRUD; find by patient/doctor.

### Models / Entities
- `src/main/java/com/hospital/model/User.java`
  - Fields: id, name/email/password, role (`ADMIN`, `DOCTOR`), profile fields.
  - Relations: to appointments, prescriptions, etc. via mapped fields.
  - Flow: Auth, dashboards, doctor lookups.

- `src/main/java/com/hospital/model/Patient.java`
  - Fields: identifiers, personal/contact info, status, gender, blood group, medical history, insurance; optional `doctor`.
  - Flow: Central to patient management, records, appointments.

- `src/main/java/com/hospital/model/Department.java`
  - Fields: id, name, description; referenced by users/rooms/services.

- `src/main/java/com/hospital/model/Room.java`
  - Fields: id, number, type, status/availability; used in patient allocation features.

- `src/main/java/com/hospital/model/Appointment.java`
  - Fields: id, patient, doctor, time, status.

- `src/main/java/com/hospital/model/MedicalRecord.java`
  - Fields: id, patient, notes, attachments/diagnoses.

- `src/main/java/com/hospital/model/Prescription.java`
  - Fields: id, patient, doctor, medicines, dosage, instructions.

### Templates (Thymeleaf)
- `src/main/resources/templates/login.html`
  - Login form that posts to `/login`.

- `src/main/resources/templates/signup.html`
  - Optional signup form (if enabled by controller logic).

- `src/main/resources/templates/dashboard.html`
  - Generic dashboard shell used/shared by role-specific pages (depends on implementation).

- `src/main/resources/templates/admin-dashboard.html`
  - Admin home with KPIs/links to admin modules.

- `src/main/resources/templates/doctor-dashboard.html`
  - Doctor home with assigned patients/appointments.

- `src/main/resources/templates/patient-management.html`
  - Admin patient list with filters, create form, actions; consumes model attributes from `PatientController.patientManagement`.

- `src/main/resources/templates/patient-details.html`
  - Detailed view/edit for a single patient; consumes model attributes from `PatientController.patientDetails`.

- `src/main/resources/templates/user-management.html`
  - Admin UI for creating/updating users (doctors), role assignment.

- `src/main/resources/templates/department-management.html`
  - Admin UI for departments.

- `src/main/resources/templates/room-management.html`
  - Admin UI for rooms.

- `src/main/resources/templates/database-management.html`
  - Admin utilities for DB tasks.

- `src/main/resources/templates/system-settings.html`
  - UI for system-wide settings.

### How Things Connect (per request)
1) Controller validates session/role (via SecurityContext + `UserService`).
2) Controller calls appropriate Service method(s).
3) Service orchestrates domain logic, delegates to Repository for persistence.
4) Service returns entities/DTOs → Controller.
5) Controller adds attributes to `Model`, returns a view name.
6) Thymeleaf template renders UI with provided attributes.

### Operational Requirements & Notes
- Java 17+, Maven 3.9+, single running instance to avoid H2 file locks.
- Security relies on `CustomUserDetailsService`, BCrypt passwords, and a working `User` table.
- Controllers that require ADMIN/DOCTOR must be reached via an authenticated session created through `/login`.
- For exports, ensure correct content types and that only authorized roles trigger the endpoints.


