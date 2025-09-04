package com.hospital.repository;

import com.hospital.model.Room;
import com.hospital.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findByStatus(Room.RoomStatus status);
    List<Room> findByRoomType(Room.RoomType roomType);
    List<Room> findByDepartment(Department department);
    List<Room> findByFloor(String floor);
    List<Room> findByBuilding(String building);
    boolean existsByRoomNumber(String roomNumber);
    
    @Query("SELECT r FROM Room r ORDER BY r.building ASC, r.floor ASC, r.roomNumber ASC")
    List<Room> findAllOrderByLocation();
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.status = :status")
    long countByStatus(Room.RoomStatus status);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.roomType = :roomType")
    long countByRoomType(Room.RoomType roomType);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.department = :department")
    long countByDepartment(Department department);
}