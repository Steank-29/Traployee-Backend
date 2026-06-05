package com.traployee.service;

import com.traployee.model.Assignment;
import com.traployee.model.Employee;
import com.traployee.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {
    
    @Autowired
    private AssignmentRepository assignmentRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private EmployeeService employeeService;
    
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }
    
    public Optional<Assignment> getAssignmentById(String id) {
        return assignmentRepository.findById(id);
    }
    
    public List<Assignment> getAssignmentsByEmployee(String employeeId) {
        return assignmentRepository.findByEmployeeId(employeeId);
    }
    
    public List<Assignment> getAssignmentsByStatus(String status) {
        return assignmentRepository.findByStatus(status);
    }
    
    public Assignment createAssignment(Assignment assignment) {
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        if (assignment.getStatus() == null || assignment.getStatus().isEmpty()) {
            assignment.setStatus("pending");
        }
        
        Assignment saved = assignmentRepository.save(assignment);
        
        // Send email notification to employee
        try {
            Optional<Employee> employee = employeeService.getEmployeeById(assignment.getEmployeeId());
            if (employee.isPresent() && employee.get().getEmail() != null && !employee.get().getEmail().isEmpty()) {
                emailService.sendAssignmentNotification(saved, employee.get());
                System.out.println("✅ Assignment notification email sent to: " + employee.get().getEmail());
            } else {
                System.out.println("⚠️ No email found for employee: " + assignment.getEmployeeId());
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to send email notification: " + e.getMessage());
        }
        
        return saved;
    }
    
    public Assignment updateAssignment(String id, Assignment assignmentDetails) {
        Optional<Assignment> existingOpt = assignmentRepository.findById(id);
        if (existingOpt.isPresent()) {
            Assignment existing = existingOpt.get();
            
            // Track if employee changed for notification
            boolean employeeChanged = !existing.getEmployeeId().equals(assignmentDetails.getEmployeeId());
            String oldEmployeeId = existing.getEmployeeId();
            
            existing.setTitle(assignmentDetails.getTitle());
            existing.setDescription(assignmentDetails.getDescription());
            existing.setEmployeeId(assignmentDetails.getEmployeeId());
            existing.setEmployeeName(assignmentDetails.getEmployeeName());
            existing.setDueDate(assignmentDetails.getDueDate());
            existing.setStatus(assignmentDetails.getStatus());
            existing.setUpdatedAt(LocalDateTime.now());
            
            Assignment saved = assignmentRepository.save(existing);
            
            // Send email notification if employee changed
            if (employeeChanged) {
                try {
                    Optional<Employee> newEmployee = employeeService.getEmployeeById(assignmentDetails.getEmployeeId());
                    if (newEmployee.isPresent() && newEmployee.get().getEmail() != null && !newEmployee.get().getEmail().isEmpty()) {
                        emailService.sendAssignmentNotification(saved, newEmployee.get());
                        System.out.println("✅ Assignment reassignment email sent to: " + newEmployee.get().getEmail());
                    }
                } catch (Exception e) {
                    System.err.println("❌ Failed to send reassignment email: " + e.getMessage());
                }
            }
            
            return saved;
        }
        return null;
    }
    
    @Transactional
    public boolean updateStatus(String id, String status) {
        Optional<Assignment> existingOpt = assignmentRepository.findById(id);
        if (existingOpt.isPresent()) {
            String oldStatus = existingOpt.get().getStatus();
            int updated = assignmentRepository.updateStatus(id, status);
            
            if (updated > 0 && !oldStatus.equals(status)) {
                // Send email notification about status change
                try {
                    Optional<Employee> employee = employeeService.getEmployeeById(existingOpt.get().getEmployeeId());
                    if (employee.isPresent() && employee.get().getEmail() != null && !employee.get().getEmail().isEmpty()) {
                        emailService.sendAssignmentStatusUpdate(existingOpt.get(), employee.get(), oldStatus, status);
                        System.out.println("✅ Status update email sent to: " + employee.get().getEmail());
                    }
                } catch (Exception e) {
                    System.err.println("❌ Failed to send status update email: " + e.getMessage());
                }
            }
            return updated > 0;
        }
        return false;
    }
    
    @Transactional
    public boolean addEmployeeReply(String id, String reply) {
        int updated = assignmentRepository.addEmployeeReply(id, reply);
        if (updated > 0) {
            // Notify admin about employee reply
            try {
                Optional<Assignment> assignment = assignmentRepository.findById(id);
                if (assignment.isPresent()) {
                    // You can implement admin notification here
                    System.out.println("📧 Employee replied to assignment: " + id);
                }
            } catch (Exception e) {
                System.err.println("Failed to notify admin: " + e.getMessage());
            }
        }
        return updated > 0;
    }
    
    @Transactional
    public boolean addAdminReply(String id, String reply, String newStatus) {
        int updated = assignmentRepository.addAdminReply(id, reply, newStatus);
        if (updated > 0) {
            // Send email notification about admin reply
            try {
                Optional<Assignment> assignment = assignmentRepository.findById(id);
                if (assignment.isPresent()) {
                    Optional<Employee> employee = employeeService.getEmployeeById(assignment.get().getEmployeeId());
                    if (employee.isPresent() && employee.get().getEmail() != null && !employee.get().getEmail().isEmpty()) {
                        emailService.sendAdminReplyNotification(assignment.get(), employee.get(), reply, newStatus);
                        System.out.println("✅ Admin reply email sent to: " + employee.get().getEmail());
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to send admin reply email: " + e.getMessage());
            }
        }
        return updated > 0;
    }
    
    @Transactional
    public boolean submitAssignment(String id, String fileName, String submissionNote) {
        int updated = assignmentRepository.submitAssignment(id, fileName, submissionNote);
        if (updated > 0) {
            // Notify admin about submission
            try {
                Optional<Assignment> assignment = assignmentRepository.findById(id);
                if (assignment.isPresent()) {
                    System.out.println("📎 Assignment submitted: " + id + " by " + assignment.get().getEmployeeName());
                    // You can implement admin email notification here
                }
            } catch (Exception e) {
                System.err.println("Failed to notify admin: " + e.getMessage());
            }
        }
        return updated > 0;
    }
    
    @Transactional
    public boolean deleteAssignment(String id) {
        if (assignmentRepository.existsById(id)) {
            assignmentRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public long getTotalCount() {
        return assignmentRepository.count();
    }
    
    public long getCountByStatus(String status) {
        return assignmentRepository.countByStatus(status);
    }
}