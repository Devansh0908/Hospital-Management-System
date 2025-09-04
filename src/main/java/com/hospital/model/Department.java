package com.hospital.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column
    private String location;
    
    @Column
    private String phoneNumber;
    
    @Column
    private String email;
    
    @ManyToOne
    @JoinColumn(name = "head_of_department_id")
    private User headOfDepartment;
    
    @Column(nullable = false)
    private LocalDateTime createdDate;
    
    @Column
    private LocalDateTime updatedDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepartmentStatus status;
    
    @Column
    private Integer capacity;
    
    @Column
    private String specialization;
    
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> doctors;
    
    // Constructors
    public Department() {
        this.createdDate = LocalDateTime.now();
        this.status = DepartmentStatus.ACTIVE;
    }
    
    public Department(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public User getHeadOfDepartment() { return headOfDepartment; }
    public void setHeadOfDepartment(User headOfDepartment) { this.headOfDepartment = headOfDepartment; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
    
    public DepartmentStatus getStatus() { return status; }
    public void setStatus(DepartmentStatus status) { this.status = status; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public List<User> getDoctors() { return doctors; }
    public void setDoctors(List<User> doctors) { this.doctors = doctors; }
    
    public enum DepartmentStatus {
        ACTIVE, INACTIVE, UNDER_MAINTENANCE
    }
}