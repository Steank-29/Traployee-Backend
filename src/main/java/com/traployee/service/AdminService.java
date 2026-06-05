package com.traployee.service;

import com.traployee.model.Admin;
import com.traployee.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    // Get all admins
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }
    
    // Get admin by ID
    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }
    
    // Get admin by username
    public Optional<Admin> getAdminByUsername(String username) {
        return adminRepository.findByUsername(username);
    }
    
    // Validate admin login
    public Optional<Admin> validateAdmin(String username, String password) {
        return adminRepository.findByUsernameAndPassword(username, password);
    }
    
    // Create new admin
    public Admin createAdmin(Admin admin) {
        if (admin.getCreated_at() == null) {
            admin.setCreated_at(LocalDateTime.now());
        }
        if (admin.getStatus() == null) {
            admin.setStatus("active");
        }
        if (admin.getRole() == null) {
            admin.setRole("admin");
        }
        return adminRepository.save(admin);
    }
    
    // Update admin login time
    public void updateLastLogin(String username) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            admin.setLast_login(LocalDateTime.now());
            adminRepository.save(admin);
        }
    }
    
    // Delete admin by ID
    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }
    
    // Check if admin exists
    public boolean adminExists(String username) {
        return adminRepository.existsByUsername(username);
    }
    
    // Get total admin count
    public long getTotalAdminCount() {
        return adminRepository.count();
    }
    
    // Get active admin count
    public long getActiveAdminCount() {
        return adminRepository.countByStatus("active");
    }
}