## Hospital Management System

A Spring Boot web application for managing hospital operations such as user access (Admin/Doctor), patients, appointments, departments, rooms, prescriptions, medical records, exports, system settings, and database management. Server-side rendered UI using Thymeleaf.

### Features
- **Authentication & Authorization**: Spring Security with form login. Role-based access for `ADMIN` and `DOCTOR`.
- **Dashboards**: Admin and Doctor dashboards with role-based navigation.
- **Patient Management**: Create, search/filter, update, view details, and delete patients.
- **Appointments**: Manage appointments and doctor assignments.
- **Medical Records & Prescriptions**: Create and view associated data.
- **Departments & Rooms**: CRUD for hospital departments and rooms.
- **User Management**: Manage doctors and users (Admin only).
- **System Settings & Database Management**: App-level configuration and DB utilities.
- **Data Export**: Export reports (CSV/Excel/PDF) for supported modules.

### Tech Stack
- Java 17, Maven, Spring Boot 3
- Spring MVC, Spring Security, Spring Data JPA (Hibernate)
- H2 (file-mode) database by default
- Thymeleaf templates for UI

### Project Structure
```
pom.xml
src/
  main/
    java/com/hospital/
      HospitalManagementApplication.java
      config/SecurityConfig.java
      controller/ (Admin, Auth, Doctor, Patient, Export, ...)
      model/ (User, Patient, Department, Room, Appointment, MedicalRecord, Prescription, ...)
      repository/ (JpaRepository interfaces)
      service/ (...Service classes incl. CustomUserDetailsService, ExportService)
    resources/
      application.properties
      templates/ (login, signup, dashboards, patient-management, doctor/admin pages, etc.)
      static/ (if any css/js)
```

### Prerequisites
- JDK 17+
- Maven 3.9+

### Configuration
The default configuration is in `src/main/resources/application.properties`.

Key notes:
- App runs on port `8081` (Tomcat embedded).
- H2 database stored under `data/` as `hospitaldb.mv.db`.
- Spring Security login page at `/login`; signup at `/signup` if enabled.

### Build & Run
From the project root:
```bash
mvn clean package
mvn spring-boot:run
```

Then open:
- `http://localhost:8081/login`
- Admin dashboard: `http://localhost:8081/admin-dashboard`    
- Admin Secret Key: `HOSPITAL_ADMIN`
- Doctor dashboard: `http://localhost:8081/doctor-dashboard`
- Patient management (ADMIN): `http://localhost:8081/admin/patient-management`

If you run via the JAR:
```bash
java -jar target/hospital-management-0.0.1-SNAPSHOT.jar
```

### Security Overview
- Custom `CustomUserDetailsService` provides user details.
- Passwords are encoded with BCrypt.
- Form login at `/login`.
- On successful login, the app stores the logged-in user in the HTTP session as `loggedInUser` and `userRole`, and redirects by role:
  - ADMIN → `/admin-dashboard`
  - DOCTOR → `/doctor-dashboard`
- Authorization: most routes require authentication; public routes include `/login`, `/signup`, and static assets.

### Important Routes (UI)
- Auth: `/login`, `/signup` (if enabled), `/logout`
- Dashboards: `/admin-dashboard`, `/doctor-dashboard`
- Admin:
  - Patients: `/admin/patient-management`, `/admin/patient-details/{id}`
  - Departments: `/admin/department-management`
  - Rooms: `/admin/room-management`
  - Users: `/admin/user-management`
  - Database: `/admin/database-management`
  - System settings: `/admin/system-settings`
- Exports: endpoints under the export controller (see `ExportController` for specifics)

### Development Tips
- Controllers render Thymeleaf templates in `src/main/resources/templates`.
- Services encapsulate business logic; repositories use Spring Data JPA.
- For role-guarded pages under `/admin/**` and `/doctor/**`, ensure you are logged in with the correct role.

### Troubleshooting
- **500 on patient management after login**:
  - Ensure you logged in via `/login` (form posts to `/login`).
  - Verify the session contains a valid user and that the account has `ADMIN` role.
  - Confirm you’re using the same origin and port (e.g., always `http://localhost:8081`).

- **H2 file is locked / app won’t start**:
  - Close any other running instance of the app.
  - Stop stray Java processes and remove locks:
    - Windows PowerShell:
      ```powershell
      taskkill /f /im java.exe
      Remove-Item -Path "data" -Recurse -Force -ErrorAction SilentlyContinue
      ```
  - Re-run: `mvn spring-boot:run`.

- **Login redirects unexpectedly**:
  - Clear browser cookies to reset `JSESSIONID`.
  - Ensure Spring Security configuration is active and the login success handler is wiring the session attributes.



