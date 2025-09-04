package com.hospital.service;

import com.hospital.model.Department;
import com.hospital.model.User;
import com.hospital.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Department saveDepartment(Department department) {
        if (department.getId() != null) {
            department.setUpdatedDate(LocalDateTime.now());
        }
        return departmentRepository.save(department);
    }

    public Department findById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    public Department findByName(String name) {
        return departmentRepository.findByName(name).orElse(null);
    }

    public List<Department> findAll() {
        return departmentRepository.findAllOrderByName();
    }

    public List<Department> findByStatus(Department.DepartmentStatus status) {
        return departmentRepository.findByStatus(status);
    }

    public List<Department> findByHeadOfDepartment(User headOfDepartment) {
        return departmentRepository.findByHeadOfDepartment(headOfDepartment);
    }

    public boolean existsByName(String name) {
        return departmentRepository.existsByName(name);
    }

    public long countDoctorsByDepartment(Department department) {
        return departmentRepository.countDoctorsByDepartment(department);
    }

    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    public long getTotalDepartments() {
        return departmentRepository.count();
    }

    public long getActiveDepartments() {
        return departmentRepository.findByStatus(Department.DepartmentStatus.ACTIVE).size();
    }
}