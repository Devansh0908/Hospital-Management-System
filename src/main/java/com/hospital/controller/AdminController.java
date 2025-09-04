package com.hospital.controller;

import com.hospital.model.User;
import com.hospital.model.Department;
import com.hospital.model.Room;
import com.hospital.service.UserService;
import com.hospital.service.DepartmentService;
import com.hospital.service.RoomService;
import com.hospital.service.SystemReportService;
import com.hospital.service.DatabaseManagementService;
import com.hospital.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private SystemReportService systemReportService;
    
    @Autowired
    private DatabaseManagementService databaseManagementService;
    
    @Autowired
    private SystemSettingsService systemSettingsService;

    @GetMapping("/user-management")
    public String userManagement(Model model, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("totalUsers", userService.getTotalUsers());
        model.addAttribute("totalDoctors", userService.getTotalDoctors());
        model.addAttribute("totalAdmins", userService.getTotalAdmins());
        model.addAttribute("allDepartments", departmentService.findAll());
        return "user-management";
    }

    @GetMapping("/department-management")
    public String departmentManagement(Model model, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allDepartments", departmentService.findAll());
        model.addAttribute("totalDepartments", departmentService.getTotalDepartments());
        model.addAttribute("activeDepartments", departmentService.getActiveDepartments());
        model.addAttribute("allDoctors", userService.getAllDoctors());
        return "department-management";
    }

    @PostMapping("/create-user")
    public String createUser(@RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String role,
                           @RequestParam(required = false) Long departmentId,
                           @RequestParam(required = false) String phoneNumber,
                           @RequestParam(required = false) String specialization,
                           @RequestParam(required = false) String licenseNumber,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) request.getSession().getAttribute("loggedInUser");
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                return "redirect:/login";
            }

            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            
            User newUser = new User(firstName, lastName, email, password, userRole);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setSpecialization(specialization);
            newUser.setLicenseNumber(licenseNumber);
            
            if (departmentId != null) {
                Department department = departmentService.findById(departmentId);
                newUser.setDepartment(department);
            }

            userService.createUser(newUser);
            redirectAttributes.addFlashAttribute("success", "User created successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating user: " + e.getMessage());
        }
        
        return "redirect:/admin/user-management";
    }

    @PostMapping("/update-user-status")
    public String updateUserStatus(@RequestParam Long userId,
                                 @RequestParam String status,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) request.getSession().getAttribute("loggedInUser");
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                return "redirect:/login";
            }

            User user = userService.findById(userId);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
                return "redirect:/admin/user-management";
            }

            user.setStatus(User.UserStatus.valueOf(status.toUpperCase()));
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("success", "User status updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user status: " + e.getMessage());
        }
        
        return "redirect:/admin/user-management";
    }

    @PostMapping("/create-department")
    public String createDepartment(@RequestParam String name,
                                 @RequestParam String description,
                                 @RequestParam(required = false) String location,
                                 @RequestParam(required = false) String phoneNumber,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) Long headOfDepartmentId,
                                 @RequestParam(required = false) Integer capacity,
                                 @RequestParam(required = false) String specialization,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) request.getSession().getAttribute("loggedInUser");
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                return "redirect:/login";
            }

            if (departmentService.existsByName(name)) {
                redirectAttributes.addFlashAttribute("error", "Department with this name already exists!");
                return "redirect:/admin/department-management";
            }

            Department department = new Department(name, description);
            department.setLocation(location);
            department.setPhoneNumber(phoneNumber);
            department.setEmail(email);
            department.setCapacity(capacity);
            department.setSpecialization(specialization);
            
            if (headOfDepartmentId != null) {
                User headOfDepartment = userService.findById(headOfDepartmentId);
                department.setHeadOfDepartment(headOfDepartment);
            }

            departmentService.saveDepartment(department);
            redirectAttributes.addFlashAttribute("success", "Department created successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating department: " + e.getMessage());
        }
        
        return "redirect:/admin/department-management";
    }

    @PostMapping("/update-department-status")
    public String updateDepartmentStatus(@RequestParam Long departmentId,
                                       @RequestParam String status,
                                       HttpServletRequest request,
                                       RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) request.getSession().getAttribute("loggedInUser");
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                return "redirect:/login";
            }

            Department department = departmentService.findById(departmentId);
            if (department == null) {
                redirectAttributes.addFlashAttribute("error", "Department not found!");
                return "redirect:/admin/department-management";
            }

            department.setStatus(Department.DepartmentStatus.valueOf(status.toUpperCase()));
            department.setUpdatedDate(LocalDateTime.now());
            departmentService.saveDepartment(department);
            redirectAttributes.addFlashAttribute("success", "Department status updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating department status: " + e.getMessage());
        }
        
        return "redirect:/admin/department-management";
    }

    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam Long userId,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) request.getSession().getAttribute("loggedInUser");
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                return "redirect:/login";
            }

            if (userId.equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "You cannot delete your own account!");
                return "redirect:/admin/user-management";
            }

            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        
        return "redirect:/admin/user-management";
    }

    @GetMapping("/room-management")
    public String roomManagement(Model model, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allRooms", roomService.findAll());
        model.addAttribute("totalRooms", roomService.getTotalRooms());
        model.addAttribute("availableRooms", roomService.countByStatus(Room.RoomStatus.AVAILABLE));
        model.addAttribute("occupiedRooms", roomService.countByStatus(Room.RoomStatus.OCCUPIED));
        model.addAttribute("maintenanceRooms", roomService.countByStatus(Room.RoomStatus.MAINTENANCE));
        model.addAttribute("allDepartments", departmentService.findAll());
        return "room-management";
    }

    @GetMapping("/system-reports")
    public String systemReports(Model model, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("systemOverview", systemReportService.generateSystemOverviewReport());
        model.addAttribute("departmentReport", systemReportService.generateDepartmentReport());
        model.addAttribute("roomUtilization", systemReportService.generateRoomUtilizationReport());
        model.addAttribute("userActivity", systemReportService.generateUserActivityReport());
        model.addAttribute("appointmentReport", systemReportService.generateAppointmentReport());
        return "system-reports";
    }

    @PostMapping("/create-room")
    public String createRoom(@RequestParam String roomNumber,
                           @RequestParam String roomType,
                           @RequestParam(required = false) Long departmentId,
                           @RequestParam(required = false) String floor,
                           @RequestParam(required = false) String building,
                           @RequestParam(required = false) Integer capacity,
                           @RequestParam(required = false) String description,
                           @RequestParam(required = false) String equipment,
                           @RequestParam(required = false) Double dailyRate,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) request.getSession().getAttribute("loggedInUser");
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                return "redirect:/login";
            }

            if (roomService.existsByRoomNumber(roomNumber)) {
                redirectAttributes.addFlashAttribute("error", "Room with this number already exists!");
                return "redirect:/admin/room-management";
            }

            Room room = new Room(roomNumber, Room.RoomType.valueOf(roomType.toUpperCase()));
            room.setFloor(floor);
            room.setBuilding(building);
            room.setCapacity(capacity);
            room.setDescription(description);
            room.setEquipment(equipment);
            room.setDailyRate(dailyRate);
            
            if (departmentId != null) {
                Department department = departmentService.findById(departmentId);
                room.setDepartment(department);
            }

            roomService.saveRoom(room);
            redirectAttributes.addFlashAttribute("success", "Room created successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating room: " + e.getMessage());
        }
        
        return "redirect:/admin/room-management";
    }

    @PostMapping("/update-room-status")
    public String updateRoomStatus(@RequestParam Long roomId,
                                 @RequestParam String status,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) request.getSession().getAttribute("loggedInUser");
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                return "redirect:/login";
            }

            Room room = roomService.findById(roomId);
            if (room == null) {
                redirectAttributes.addFlashAttribute("error", "Room not found!");
                return "redirect:/admin/room-management";
            }

            room.setStatus(Room.RoomStatus.valueOf(status.toUpperCase()));
            room.setUpdatedDate(LocalDateTime.now());
            roomService.saveRoom(room);
            redirectAttributes.addFlashAttribute("success", "Room status updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating room status: " + e.getMessage());
        }
        
        return "redirect:/admin/room-management";
    }

    @PostMapping("/mark-room-cleaned")
    public String markRoomCleaned(@RequestParam Long roomId,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) request.getSession().getAttribute("loggedInUser");
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                return "redirect:/login";
            }

            Room room = roomService.markCleaned(roomId);
            if (room != null) {
                redirectAttributes.addFlashAttribute("success", "Room marked as cleaned and available!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Room not found!");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating room: " + e.getMessage());
        }
        
        return "redirect:/admin/room-management";
    }

    @GetMapping("/database-management")
    public String databaseManagement(Model model, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("databaseStats", databaseManagementService.getDatabaseStatistics());
        model.addAttribute("databaseHealth", databaseManagementService.getDatabaseHealth());
        model.addAttribute("recentActivity", databaseManagementService.getRecentDatabaseActivity());
        return "database-management";
    }

    @GetMapping("/system-settings")
    public String systemSettings(Model model, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allSettings", systemSettingsService.getAllSettings());
        model.addAttribute("systemInfo", systemSettingsService.getSystemInfo());
        model.addAttribute("systemLogs", systemSettingsService.getSystemLogs());
        return "system-settings";
    }

    @PostMapping("/database-backup")
    public String performDatabaseBackup(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }

        try {
            boolean success = databaseManagementService.performDatabaseBackup();
            if (success) {
                redirectAttributes.addFlashAttribute("success", "Database backup completed successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Database backup failed!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error during backup: " + e.getMessage());
        }
        
        return "redirect:/admin/database-management";
    }

    @PostMapping("/database-optimize")
    public String optimizeDatabase(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }

        try {
            boolean success = databaseManagementService.optimizeDatabase();
            if (success) {
                redirectAttributes.addFlashAttribute("success", "Database optimization completed successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Database optimization failed!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error during optimization: " + e.getMessage());
        }
        
        return "redirect:/admin/database-management";
    }

    @PostMapping("/database-cleanup")
    public String cleanupDatabase(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }

        try {
            boolean success = databaseManagementService.cleanupDatabase();
            if (success) {
                redirectAttributes.addFlashAttribute("success", "Database cleanup completed successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Database cleanup failed!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error during cleanup: " + e.getMessage());
        }
        
        return "redirect:/admin/database-management";
    }

    @PostMapping("/update-settings")
    public String updateSystemSettings(@RequestParam Map<String, String> settings,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }

        try {
            boolean success = systemSettingsService.updateMultipleSettings(settings);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "System settings updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update system settings!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating settings: " + e.getMessage());
        }
        
        return "redirect:/admin/system-settings";
    }

    @PostMapping("/reset-settings")
    public String resetSystemSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }

        try {
            boolean success = systemSettingsService.resetToDefaults();
            if (success) {
                redirectAttributes.addFlashAttribute("success", "System settings reset to defaults successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to reset system settings!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error resetting settings: " + e.getMessage());
        }
        
        return "redirect:/admin/system-settings";
    }

    @PostMapping("/export-settings")
    public String exportSystemSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }

        try {
            boolean success = systemSettingsService.exportSettings();
            if (success) {
                redirectAttributes.addFlashAttribute("success", "System settings exported successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to export system settings!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error exporting settings: " + e.getMessage());
        }
        
        return "redirect:/admin/system-settings";
    }
}