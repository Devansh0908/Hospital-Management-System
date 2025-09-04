package com.hospital.repository;

import com.hospital.model.Department;
import com.hospital.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByName(String name);
    List<Department> findByStatus(Department.DepartmentStatus status);
    List<Department> findByHeadOfDepartment(User headOfDepartment);
    boolean existsByName(String name);
    
    @Query("SELECT d FROM Department d ORDER BY d.name ASC")
    List<Department> findAllOrderByName();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.department = :department AND u.role = 'DOCTOR'")
    long countDoctorsByDepartment(Department department);
}