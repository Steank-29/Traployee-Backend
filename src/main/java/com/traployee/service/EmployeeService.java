package com.traployee.service;

import com.traployee.model.Employee;
import com.traployee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    // Get all employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    // Get employee by ID
    public Optional<Employee> getEmployeeById(String id) {
        return employeeRepository.findById(id);
    }
    
    // Validate employee login (check ID and Name match)
    public Optional<Employee> validateEmployee(String id, String name) {
        return employeeRepository.findByIdAndName(id, name);
    }
    
    // Create or update employee
    public Employee saveEmployee(Employee employee) {
        if (employee.getCreatedAt() == null) {
            employee.setCreatedAt(LocalDateTime.now());
        }
        employee.setUpdatedAt(LocalDateTime.now());
        employee.setLastLogin(LocalDateTime.now());
        return employeeRepository.save(employee);
    }
    
    // Register a new employee
    public Employee registerEmployee(Employee employee) {
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        employee.setLastLogin(LocalDateTime.now());
        employee.setTotalSessions(0);
        employee.setTotalTime(0L);
        employee.setTotalKeystrokes(0);
        employee.setTotalMouseClicks(0);
        employee.setTotalMouseMovements(0);
        employee.setStatus("active");
        return employeeRepository.save(employee);
    }
    
    // Update employee
    @Transactional
    public Employee updateEmployee(String id, Employee employeeDetails) {
        Optional<Employee> existingOpt = employeeRepository.findById(id);
        if (existingOpt.isPresent()) {
            Employee existing = existingOpt.get();
            existing.setName(employeeDetails.getName());
            existing.setEmail(employeeDetails.getEmail());
            existing.setDepartment(employeeDetails.getDepartment());
            existing.setPhone(employeeDetails.getPhone());
            existing.setPosition(employeeDetails.getPosition());
            existing.setLocation(employeeDetails.getLocation());
            existing.setJoinDate(employeeDetails.getJoinDate());
            existing.setSkills(employeeDetails.getSkills());
            existing.setBio(employeeDetails.getBio());
            existing.setStatus(employeeDetails.getStatus());
            existing.setUpdatedAt(LocalDateTime.now());
            return employeeRepository.save(existing);
        }
        return null;
    }
    
// Delete employee
@Transactional
public boolean deleteEmployee(String id) {
    try {
        int deletedCount = employeeRepository.deleteEmployeeById(id);
        
        if (deletedCount > 0) {
            System.out.println("✅ Employee deleted from database: " + id + " (Rows affected: " + deletedCount + ")");
            return true;
        } else {
            System.out.println("❌ Employee not found: " + id);
            return false;
        }
    } catch (Exception e) {
        System.out.println("❌ Delete error: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
// Check if employee exists
    public boolean employeeExists(String id) {
        return employeeRepository.existsById(id);
    }
    
    // Get total employee count
    public long getTotalEmployeeCount() {
        return employeeRepository.count();
    }
    
    // Get active employee count
    public long getActiveEmployeeCount() {
        return employeeRepository.countByStatus("active");
    }
}