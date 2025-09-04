package com.hospital.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String roomNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;
    
    @Column
    private String floor;
    
    @Column
    private String building;
    
    @Column
    private Integer capacity;
    
    @Column
    private String description;
    
    @Column
    private String equipment;
    
    @Column
    private Double dailyRate;
    
    @ManyToOne
    @JoinColumn(name = "current_patient_id")
    private Patient currentPatient;
    
    @Column
    private LocalDateTime lastCleaned;
    
    @Column
    private LocalDateTime lastMaintenance;
    
    @Column
    private LocalDateTime createdDate;
    
    @Column
    private LocalDateTime updatedDate;
    
    // Constructors
    public Room() {
        this.createdDate = LocalDateTime.now();
        this.status = RoomStatus.AVAILABLE;
    }
    
    public Room(String roomNumber, RoomType roomType) {
        this();
        this.roomNumber = roomNumber;
        this.roomType = roomType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    
    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
    
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }
    
    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }
    
    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    
    public Double getDailyRate() { return dailyRate; }
    public void setDailyRate(Double dailyRate) { this.dailyRate = dailyRate; }
    
    public Patient getCurrentPatient() { return currentPatient; }
    public void setCurrentPatient(Patient currentPatient) { this.currentPatient = currentPatient; }
    
    public LocalDateTime getLastCleaned() { return lastCleaned; }
    public void setLastCleaned(LocalDateTime lastCleaned) { this.lastCleaned = lastCleaned; }
    
    public LocalDateTime getLastMaintenance() { return lastMaintenance; }
    public void setLastMaintenance(LocalDateTime lastMaintenance) { this.lastMaintenance = lastMaintenance; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
    
    public enum RoomType {
        GENERAL_WARD, PRIVATE_ROOM, ICU, EMERGENCY, OPERATING_ROOM, 
        CONSULTATION_ROOM, LABORATORY, RADIOLOGY, MATERNITY, PEDIATRIC
    }
    
    public enum RoomStatus {
        AVAILABLE, OCCUPIED, MAINTENANCE, CLEANING, OUT_OF_ORDER, RESERVED
    }
}