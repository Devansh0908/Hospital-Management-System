## Application Workflow and Data Flow

This document explains how requests move through the system, how data is fetched and persisted, and how UI pages are rendered. It is organized by cross-cutting flows (request lifecycle, authentication) and by core features (dashboards, patient management, exports, settings, etc.).

### 1) Request Lifecycle (MVC)
- Browser sends HTTP request to the server (Tomcat 10 at port 8081)
- Spring Security filter chain intercepts the request
  - If authentication is required and missing, user is redirected to `/login`
  - If authenticated, the request proceeds
- Spring MVC dispatches to a `@Controller` method (mapped by `@GetMapping` / `@PostMapping`)
- Controller coordinates with one or more `@Service` classes
- Services call `@Repository` interfaces (Spring Data JPA) to read/write entities
- Controller puts data into the `Model`
- Controller returns a logical view name → Thymeleaf renders an `.html` template under `src/main/resources/templates`

Key participants:
- Controllers: `com.hospital.controller.*`
- Services: `com.hospital.service.*`
- Repositories: `com.hospital.repository.*` (JPA)
- Models/Entities: `com.hospital.model.*`
- Templates: `src/main/resources/templates/*.html`

### 2) Authentication and Authorization Flow
- Login Page: `GET /login` renders the login form (`templates/login.html`)
- Submit Credentials: `POST /login` handled by Spring Security
  - `CustomUserDetailsService` loads user by email/username
  - Password verified with BCrypt (`PasswordEncoder`)
  - On success, a login success handler in `SecurityConfig`:
    - Resolves the authenticated `User` via `UserService.findByEmail`
    - Stores user in session: `loggedInUser`, and role string in `userRole`
    - Redirects by role:
      - `ADMIN` → `/admin-dashboard`
      - `DOCTOR` → `/doctor-dashboard`
- Authorization Rules (in `SecurityConfig`):
  - `/login`, `/signup`, and static assets are open
  - all other routes require authentication; admin/doctor sections require the correct role at the controller level

Session state used by controllers:
- `loggedInUser`: the full `User` entity of the current session
- `userRole`: the string role, e.g., `ADMIN` or `DOCTOR`

### 3) Dashboards
- Admin Dashboard: `GET /admin-dashboard`
  - Controller collects high-level metrics and the current `loggedInUser`
  - Model attributes rendered in `templates/admin-dashboard.html`
- Doctor Dashboard: `GET /doctor-dashboard`
  - Similar pattern; scoped to the doctor’s assignments and patients

Data flow:
- Controller → Service(s) → Repository (read) → Entities returned
- Controller → Model attributes → Thymeleaf template

### 4) Patient Management (ADMIN)
Routes:
- `GET /admin/patient-management`
  - Validates session user is ADMIN
  - Accepts optional filters: `search`, `status`, `gender`, `bloodGroup`, `doctorId`
  - Service method decides between `findAllOrderByName()` or `findPatientsWithFilters(...)`
  - Loads all doctors for filter dropdowns
  - Model attributes: `currentUser`, `allPatients`, `patientStatistics`, `allDoctors`, and filter persistence fields
  - Renders `templates/patient-management.html`

- `POST /admin/create-patient`
  - Validates ADMIN session
  - Validates duplicate email via `patientService.existsByEmail`
  - Maps form fields → new `Patient` entity, optional associations (doctor)
  - Persists via `patientService.savePatient`
  - Redirects with flash message to `/admin/patient-management`

- `GET /admin/patient-details/{id}`
  - Validates ADMIN session
  - Loads `Patient` by id, adds `currentUser` and `patient` to model
  - Renders `templates/patient-details.html`

- `POST /admin/update-patient`
  - Validates ADMIN session
  - Loads patient, checks for email uniqueness if changed
  - Updates mutable fields and saves
  - Redirects with flash message back to `/admin/patient-details/{id}`

- `POST /admin/delete-patient`
  - Validates ADMIN session
  - Deletes patient by id
  - Redirects with flash message back to `/admin/patient-management`

Data Flow Summary (Patient):
- Create/Update/Delete: Controller → PatientService → PatientRepository → DB
- Read/List/Details: Controller → PatientService (+UserService for doctor list) → Repos → Controller → Model → Template

### 5) Appointments, Medical Records, Prescriptions
- Similar controller → service → repository pattern
- Entities: `Appointment`, `MedicalRecord`, `Prescription`
- Typical operations: create, assign doctor, list by patient, update status, view details

Data Flow:
- Controller validates role
- Service orchestrates: may fetch `Patient`, `User`(doctor), and the target entity
- Repository saves/retrieves entities
- Template renders lists and detail views

### 6) Departments and Rooms (ADMIN)
- Department routes under admin; typical CRUD
- Room routes under admin; typical CRUD
- Data flow mirrors Patients: Controller → DepartmentService/RoomService → Repository → DB

### 7) User Management (ADMIN)
- Manage doctors and users
- Create/update user records (role assignment, basic profile)
- Services wrap repository operations; password handling via encoder where applicable

### 8) Exports
- `ExportController` exposes endpoints to generate CSV/Excel/PDF outputs for supported modules
- Flow:
  - Controller parses filters/parameters
  - Calls `ExportService` which queries via services/repositories
  - `ExportService` formats rows and streams or writes a file response (content-type set by controller)

### 9) System Settings & Database Management
- System settings page reads/writes configuration stored in DB (or application state where implemented)
- Database management utility endpoints let ADMIN export/backup or reset certain datasets (guarded by role)

### 10) Error Handling and Feedback
- Redirects with Flash Attributes (`RedirectAttributes`) for success/error banners on the next view
- 4xx/5xx fall back to Spring Boot Whitelabel page if no custom error view is provided
- Common causes:
  - Missing session or wrong role → redirect to `/login`
  - Validation issues (duplicate emails, missing entities) → flash error + redirect

### 11) Security Configuration Details
- Located in `config/SecurityConfig.java`
- Key elements:
  - `PasswordEncoder` = BCrypt
  - `DaoAuthenticationProvider` wired with `CustomUserDetailsService`
  - `SecurityFilterChain` defines:
    - Public endpoints: `/login`, `/signup`, static assets
    - `formLogin().loginPage("/login")`
    - Custom success handler:
      - Resolves `User` record and stores it in session `loggedInUser`
      - Redirects to role-based dashboards
    - `logout` at `/logout`
    - `csrf` disabled for simplicity (re-enable as needed)

Controller side role checks:
- Many admin endpoints validate the session `loggedInUser` and role. After the security changes, some controllers also read the `Authentication` from the `SecurityContextHolder` to resolve the email, then fetch the `User` via `UserService` and confirm role.

### 12) Persistence Layer
- Spring Data JPA repositories per entity, e.g. `PatientRepository`, `UserRepository`, etc.
- Common patterns:
  - `findById`, `findAll`, custom queries for filtering/sorting
  - Service methods encapsulate transactions and validations, then call repos

### 13) Rendering Layer (Thymeleaf)
- Each controller method returns a view name mapped to an `.html` template
- The `Model` contains attributes used by the template (lists, entities, current user, filters)
- Templates leverage conditionals/loops to render data tables, forms, flash messages

### 14) Database (H2, file mode)
- H2 DB file path (default) under `data/hospitaldb.mv.db`
- On Windows, ensure only one application instance is running to avoid file locks
- Troubleshooting lock issues:
  - Kill stray `java.exe` processes
  - Remove the `data` directory if you want a clean start (will drop data!)

### 15) End-to-End Example: Admin Opens Patient Management
1. Browser: `GET /admin/patient-management`
2. Security Filter: checks authentication; if not logged in → redirect `/login`
3. Controller: `PatientController.patientManagement(...)`
   - Gets `Authentication` → email → `UserService.findByEmail`
   - Confirms role `ADMIN`
   - Reads filters from query params
   - `PatientService` loads patients list (filtered or full)
   - `UserService.getAllDoctors()` for filter dropdown
   - `patientService.getPatientStatistics()` for metrics
   - Adds attributes to `Model`
4. View: returns `patient-management` → Thymeleaf renders `templates/patient-management.html`

### 16) End-to-End Example: Create Patient
1. Browser: `POST /admin/create-patient` with form fields
2. Security/Controller: validates `ADMIN`
3. Controller: duplicate email check via `patientService.existsByEmail`
4. Controller: maps fields → `Patient` entity, optional doctor association
5. Service: `patientService.savePatient` → Repo persists
6. Redirect: `/admin/patient-management` with success flash message

### 17) Notes for Extensibility
- Keep controllers thin; push validation and business rules into services
- Prefer constructor injection for testability
- Add integration tests around critical flows (auth, patient CRUD)
- Re-enable CSRF and add explicit role annotations as needed in production


