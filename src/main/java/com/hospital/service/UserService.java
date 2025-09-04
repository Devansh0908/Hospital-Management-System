package com.hospital.service;

import com.hospital.model.User;
import com.hospital.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private static final String ADMIN_KEY = "HOSPITAL_ADMIN";
    
    public User registerUser(String firstName, String lastName, String email, String password, User.Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        
        return userRepository.save(user);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    public boolean validateAdminKey(String adminKey) {
        return ADMIN_KEY.equals(adminKey);
    }
    
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public long getTotalUsers() {
        return userRepository.count();
    }
    
    public long getTotalDoctors() {
        return userRepository.countByRole(User.Role.DOCTOR);
    }
    
    public long getTotalAdmins() {
        return userRepository.countByRole(User.Role.ADMIN);
    }
    
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public java.util.List<User> getAllDoctors() {
        return userRepository.findByRole(User.Role.DOCTOR);
    }
    
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    public java.util.List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
}