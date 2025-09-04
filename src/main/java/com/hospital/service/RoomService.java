package com.hospital.service;

import com.hospital.model.Room;
import com.hospital.model.Department;
import com.hospital.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public Room saveRoom(Room room) {
        if (room.getId() != null) {
            room.setUpdatedDate(LocalDateTime.now());
        }
        return roomRepository.save(room);
    }

    public Room findById(Long id) {
        return roomRepository.findById(id).orElse(null);
    }

    public Room findByRoomNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber).orElse(null);
    }

    public List<Room> findAll() {
        return roomRepository.findAllOrderByLocation();
    }

    public List<Room> findByStatus(Room.RoomStatus status) {
        return roomRepository.findByStatus(status);
    }

    public List<Room> findByRoomType(Room.RoomType roomType) {
        return roomRepository.findByRoomType(roomType);
    }

    public List<Room> findByDepartment(Department department) {
        return roomRepository.findByDepartment(department);
    }

    public List<Room> findByFloor(String floor) {
        return roomRepository.findByFloor(floor);
    }

    public List<Room> findByBuilding(String building) {
        return roomRepository.findByBuilding(building);
    }

    public boolean existsByRoomNumber(String roomNumber) {
        return roomRepository.existsByRoomNumber(roomNumber);
    }

    public long getTotalRooms() {
        return roomRepository.count();
    }

    public long countByStatus(Room.RoomStatus status) {
        return roomRepository.countByStatus(status);
    }

    public long countByRoomType(Room.RoomType roomType) {
        return roomRepository.countByRoomType(roomType);
    }

    public long countByDepartment(Department department) {
        return roomRepository.countByDepartment(department);
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    public Room assignPatient(Long roomId, Long patientId) {
        Room room = findById(roomId);
        if (room != null && room.getStatus() == Room.RoomStatus.AVAILABLE) {
            // This would typically involve patient service to get the patient
            room.setStatus(Room.RoomStatus.OCCUPIED);
            room.setUpdatedDate(LocalDateTime.now());
            return saveRoom(room);
        }
        return null;
    }

    public Room releaseRoom(Long roomId) {
        Room room = findById(roomId);
        if (room != null) {
            room.setCurrentPatient(null);
            room.setStatus(Room.RoomStatus.CLEANING);
            room.setUpdatedDate(LocalDateTime.now());
            return saveRoom(room);
        }
        return null;
    }

    public Room markCleaned(Long roomId) {
        Room room = findById(roomId);
        if (room != null) {
            room.setStatus(Room.RoomStatus.AVAILABLE);
            room.setLastCleaned(LocalDateTime.now());
            room.setUpdatedDate(LocalDateTime.now());
            return saveRoom(room);
        }
        return null;
    }
}