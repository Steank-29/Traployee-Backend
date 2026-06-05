package com.traployee.controller;

import com.traployee.dto.LoginRequest;
import com.traployee.dto.LoginResponse;
import com.traployee.model.Admin;
import com.traployee.model.Assignment;
import com.traployee.model.Message;
import com.traployee.model.Screenshot;
import com.traployee.model.Employee;
import com.traployee.model.Session;
import com.traployee.repository.ScreenshotRepository;
import com.traployee.service.AdminService;
import com.traployee.service.AnalyticsService;
import com.traployee.service.AssignmentService;
import com.traployee.service.EmployeeService;
import com.traployee.service.MessageService;
import com.traployee.service.SessionService;
import com.traployee.service.SettingsService;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:1234", "http://localhost:12345"})
public class AuthController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private AdminService adminService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private ScreenshotRepository screenshotRepository;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private MessageService messageService;
    
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        
        System.out.println("Login attempt - UserType: " + request.getUserType());
        System.out.println("Login attempt - ID/Username: " + request.getId());
        
        // ========== ADMIN LOGIN (Using Database) ==========
        if ("admin".equals(request.getUserType())) {
            // Check admin credentials from database
            Optional<Admin> adminOpt = adminService.validateAdmin(request.getId(), request.getPassword());
            
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                
                // Check if admin is active
                if (!"active".equals(admin.getStatus())) {
                    System.out.println("❌ Admin login failed - Account inactive: " + request.getId());
                    return new LoginResponse(false, "Your account is inactive. Please contact system administrator.", null, null);
                }
                
                // Update last login time
                adminService.updateLastLogin(request.getId());
                
                // Prepare response data
                Map<String, Object> adminData = new HashMap<>();
                adminData.put("id", admin.getId());
                adminData.put("username", admin.getUsername());
                adminData.put("fullname", admin.getFullname());
                adminData.put("email", admin.getEmail());
                adminData.put("role", admin.getRole());
                adminData.put("image", admin.getImage());
                adminData.put("status", admin.getStatus());
                
                System.out.println("✅ Admin login successful: " + admin.getUsername() + " - " + admin.getFullname());
                return new LoginResponse(true, "Login successful", "admin", adminData);
            } else {
                System.out.println("❌ Admin login failed: " + request.getId());
                return new LoginResponse(false, "Invalid username or password", null, null);
            }
        }
        
        // ========== EMPLOYEE LOGIN ==========
        if ("employee".equals(request.getUserType())) {
            // Check if employee exists with matching ID and Name
            Optional<Employee> employeeOpt = employeeService.validateEmployee(request.getId(), request.getName());
            
            if (employeeOpt.isPresent()) {
                Employee employee = employeeOpt.get();
                
                // Check if employee is active
                if (!"active".equals(employee.getStatus())) {
                    System.out.println("❌ Employee login failed - Account inactive: " + request.getId());
                    return new LoginResponse(false, "Your account is inactive. Please contact HR.", null, null);
                }
                
                // Update last login time
                employee.setLastLogin(java.time.LocalDateTime.now());
                employeeService.saveEmployee(employee);
                
                // Prepare response data
                Map<String, Object> employeeData = new HashMap<>();
                employeeData.put("id", employee.getId());
                employeeData.put("name", employee.getName());
                employeeData.put("email", employee.getEmail());
                employeeData.put("department", employee.getDepartment());
                employeeData.put("phone", employee.getPhone());
                employeeData.put("position", employee.getPosition());
                employeeData.put("location", employee.getLocation());
                employeeData.put("joinDate", employee.getJoinDate());
                employeeData.put("skills", employee.getSkills());
                employeeData.put("bio", employee.getBio());
                employeeData.put("totalSessions", employee.getTotalSessions());
                employeeData.put("totalTime", employee.getTotalTime());
                employeeData.put("totalKeystrokes", employee.getTotalKeystrokes());
                employeeData.put("totalMouseClicks", employee.getTotalMouseClicks());
                employeeData.put("status", employee.getStatus());
                
                System.out.println("✅ Employee login successful: " + employee.getId() + " - " + employee.getName());
                return new LoginResponse(true, "Login successful", "employee", employeeData);
            } else {
                System.out.println("❌ Employee login failed: " + request.getId() + " - " + request.getName());
                return new LoginResponse(false, "Employee not found. Please check your ID and Name.", null, null);
            }
        }
        
        // Unknown user type
        return new LoginResponse(false, "Invalid user type", null, null);
    }
    
    // Register new admin (only super admin can do this)
    @PostMapping("/register/admin")
    public LoginResponse registerAdmin(@RequestBody Admin admin) {
        try {
            // Check if username already exists
            if (adminService.adminExists(admin.getUsername())) {
                return new LoginResponse(false, "Username already exists", null, null);
            }
            
            // Create new admin
            Admin savedAdmin = adminService.createAdmin(admin);
            
            Map<String, Object> adminData = new HashMap<>();
            adminData.put("id", savedAdmin.getId());
            adminData.put("username", savedAdmin.getUsername());
            adminData.put("fullname", savedAdmin.getFullname());
            adminData.put("email", savedAdmin.getEmail());
            adminData.put("role", savedAdmin.getRole());
            
            System.out.println("✅ New admin registered: " + savedAdmin.getUsername() + " - " + savedAdmin.getFullname());
            return new LoginResponse(true, "Admin registered successfully", "admin", adminData);
            
        } catch (Exception e) {
            System.out.println("❌ Registration error: " + e.getMessage());
            return new LoginResponse(false, "Registration failed: " + e.getMessage(), null, null);
        }
    }
    
    // Register new employee
    @PostMapping("/register/employee")
    public LoginResponse registerEmployee(@RequestBody Employee employee) {
        try {
            // Check if employee already exists
            if (employeeService.employeeExists(employee.getId())) {
                return new LoginResponse(false, "Employee ID already exists", null, null);
            }
            
            // Register new employee
            Employee savedEmployee = employeeService.registerEmployee(employee);
            
            Map<String, Object> employeeData = new HashMap<>();
            employeeData.put("id", savedEmployee.getId());
            employeeData.put("name", savedEmployee.getName());
            employeeData.put("email", savedEmployee.getEmail());
            employeeData.put("department", savedEmployee.getDepartment());
            employeeData.put("phone", savedEmployee.getPhone());
            employeeData.put("position", savedEmployee.getPosition());
            employeeData.put("location", savedEmployee.getLocation());
            employeeData.put("joinDate", savedEmployee.getJoinDate());
            employeeData.put("skills", savedEmployee.getSkills());
            employeeData.put("bio", savedEmployee.getBio());
            employeeData.put("status", savedEmployee.getStatus());
            
            System.out.println("✅ New employee registered: " + savedEmployee.getId() + " - " + savedEmployee.getName());
            return new LoginResponse(true, "Employee registered successfully", "admin", employeeData);
            
        } catch (Exception e) {
            System.out.println("❌ Registration error: " + e.getMessage());
            return new LoginResponse(false, "Registration failed: " + e.getMessage(), null, null);
        }
    }
    
    // Get all admins
    @GetMapping("/admins")
    public java.util.List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }
    
    // Get admin by ID
    @GetMapping("/admins/{id}")
    public Optional<Admin> getAdminById(@PathVariable Long id) {
        return adminService.getAdminById(id);
    }
    
    // Update admin status
    @PutMapping("/admins/{id}/status")
    public LoginResponse updateAdminStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        try {
            Optional<Admin> adminOpt = adminService.getAdminById(id);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                admin.setStatus(statusUpdate.get("status"));
                adminService.createAdmin(admin);
                
                System.out.println("✅ Admin status updated: " + admin.getUsername() + " -> " + admin.getStatus());
                return new LoginResponse(true, "Admin status updated successfully", "admin", null);
            } else {
                return new LoginResponse(false, "Admin not found", null, null);
            }
        } catch (Exception e) {
            return new LoginResponse(false, "Update failed: " + e.getMessage(), null, null);
        }
    }
    
    // Delete admin
    @DeleteMapping("/admins/{id}")
    public LoginResponse deleteAdmin(@PathVariable Long id) {
        try {
            adminService.deleteAdmin(id);
            System.out.println("✅ Admin deleted: ID " + id);
            return new LoginResponse(true, "Admin deleted successfully", "admin", null);
        } catch (Exception e) {
            return new LoginResponse(false, "Delete failed: " + e.getMessage(), null, null);
        }
    }
    
    // Get admin statistics
    @GetMapping("/admins/stats")
    public Map<String, Long> getAdminStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalAdmins", adminService.getTotalAdminCount());
        stats.put("activeAdmins", adminService.getActiveAdminCount());
        return stats;
    }
    
    // Get all employees
    @GetMapping("/employees")
    public java.util.List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
    
    // Get employee by ID
    @GetMapping("/employees/{id}")
    public Optional<Employee> getEmployeeById(@PathVariable String id) {
        return employeeService.getEmployeeById(id);
    }
    
    // Get employee statistics
    @GetMapping("/employees/stats")
    public Map<String, Long> getEmployeeStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalEmployees", employeeService.getTotalEmployeeCount());
        stats.put("activeEmployees", employeeService.getActiveEmployeeCount());
        return stats;
    }
    
// Update employee
@PutMapping("/employees/{id}")
public LoginResponse updateEmployee(@PathVariable String id, @RequestBody Map<String, Object> employeeData) {
    try {
        Optional<Employee> existingOpt = employeeService.getEmployeeById(id);
        if (existingOpt.isPresent()) {
            Employee existing = existingOpt.get();
            existing.setName((String) employeeData.get("name"));
            existing.setEmail((String) employeeData.get("email"));
            existing.setDepartment((String) employeeData.get("department"));
            existing.setPhone((String) employeeData.get("phone"));
            existing.setPosition((String) employeeData.get("position"));
            existing.setLocation((String) employeeData.get("location"));
            existing.setSkills((String) employeeData.get("skills"));
            existing.setBio((String) employeeData.get("bio"));
            existing.setStatus((String) employeeData.get("status"));
            
            // Handle joinDate - simple String assignment
            String joinDateStr = (String) employeeData.get("joinDate");
            existing.setJoinDate(joinDateStr != null && !joinDateStr.isEmpty() ? joinDateStr : null);
            
            existing.setUpdatedAt(LocalDateTime.now());
            Employee updated = employeeService.saveEmployee(existing);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", updated.getId());
            responseData.put("name", updated.getName());
            responseData.put("email", updated.getEmail());
            responseData.put("department", updated.getDepartment());
            responseData.put("phone", updated.getPhone());
            responseData.put("position", updated.getPosition());
            responseData.put("location", updated.getLocation());
            responseData.put("joinDate", updated.getJoinDate());
            responseData.put("skills", updated.getSkills());
            responseData.put("bio", updated.getBio());
            responseData.put("status", updated.getStatus());
            
            System.out.println("✅ Employee updated: " + updated.getId() + " - " + updated.getName());
            return new LoginResponse(true, "Employee updated successfully", "admin", responseData);
        } else {
            return new LoginResponse(false, "Employee not found", null, null);
        }
    } catch (Exception e) {
        System.out.println("❌ Update error: " + e.getMessage());
        e.printStackTrace();
        return new LoginResponse(false, "Update failed: " + e.getMessage(), null, null);
    }
}  



// Delete employee


    @DeleteMapping("/employees/{id}")
    public LoginResponse deleteEmployee(@PathVariable String id) {
        try {
            boolean deleted = employeeService.deleteEmployee(id);
            if (deleted) {
                System.out.println("✅ Employee deleted successfully: " + id);
                return new LoginResponse(true, "Employee deleted successfully", "admin", null);
            } else {
                System.out.println("❌ Employee not found: " + id);
                return new LoginResponse(false, "Employee not found with ID: " + id, null, null);
            }
        } catch (Exception e) {
            System.err.println("❌ Delete error: " + e.getMessage());
            e.printStackTrace();
            return new LoginResponse(false, "Delete failed: " + e.getMessage(), null, null);
        }
    }
    
    // Test endpoint
    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "OK");
        status.put("message", "Traployee Backend is running!");
        status.put("version", "2.0.0");
        status.put("timestamp", java.time.LocalDateTime.now().toString());
        return status;
    }


    // ========== ASSIGNMENT ENDPOINTS ==========

// Get all assignments
@GetMapping("/assignments")
public List<Assignment> getAllAssignments() {
    return assignmentService.getAllAssignments();
}

// Get assignment by ID
@GetMapping("/assignments/{id}")
public Optional<Assignment> getAssignmentById(@PathVariable String id) {
    return assignmentService.getAssignmentById(id);
}

// Get assignments by employee
@GetMapping("/assignments/employee/{employeeId}")
public List<Assignment> getAssignmentsByEmployee(@PathVariable String employeeId) {
    return assignmentService.getAssignmentsByEmployee(employeeId);
}

// Get assignments by status
@GetMapping("/assignments/status/{status}")
public List<Assignment> getAssignmentsByStatus(@PathVariable String status) {
    return assignmentService.getAssignmentsByStatus(status);
}

// Create assignment
@PostMapping("/assignments")
public LoginResponse createAssignment(@RequestBody Assignment assignment) {
    try {
        // Generate ID if not provided
        if (assignment.getId() == null || assignment.getId().isEmpty()) {
            long count = assignmentService.getTotalCount() + 1;
            assignment.setId(String.format("ASS%03d", count));
        }
        
        Assignment saved = assignmentService.createAssignment(assignment);
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", saved.getId());
        data.put("title", saved.getTitle());
        data.put("employeeId", saved.getEmployeeId());
        data.put("dueDate", saved.getDueDate());
        data.put("status", saved.getStatus());
        
        System.out.println("✅ Assignment created: " + saved.getId() + " - " + saved.getTitle());
        return new LoginResponse(true, "Assignment created successfully", "admin", data);
    } catch (Exception e) {
        System.out.println("❌ Create assignment error: " + e.getMessage());
        return new LoginResponse(false, "Creation failed: " + e.getMessage(), null, null);
    }
}

// Update assignment
@PutMapping("/assignments/{id}")
public LoginResponse updateAssignment(@PathVariable String id, @RequestBody Assignment assignment) {
    try {
        Assignment updated = assignmentService.updateAssignment(id, assignment);
        if (updated != null) {
            System.out.println("✅ Assignment updated: " + id);
            return new LoginResponse(true, "Assignment updated successfully", "admin", null);
        } else {
            return new LoginResponse(false, "Assignment not found", null, null);
        }
    } catch (Exception e) {
        return new LoginResponse(false, "Update failed: " + e.getMessage(), null, null);
    }
}

// Update assignment status
@PutMapping("/assignments/{id}/status")
public LoginResponse updateAssignmentStatus(@PathVariable String id, @RequestBody Map<String, String> statusUpdate) {
    try {
        String newStatus = statusUpdate.get("status");
        boolean updated = assignmentService.updateStatus(id, newStatus);
        if (updated) {
            System.out.println("✅ Assignment status updated: " + id + " -> " + newStatus);
            return new LoginResponse(true, "Status updated successfully", "admin", null);
        } else {
            return new LoginResponse(false, "Assignment not found", null, null);
        }
    } catch (Exception e) {
        return new LoginResponse(false, "Update failed: " + e.getMessage(), null, null);
    }
}

// Delete assignment
@DeleteMapping("/assignments/{id}")
public LoginResponse deleteAssignment(@PathVariable String id) {
    try {
        boolean deleted = assignmentService.deleteAssignment(id);
        if (deleted) {
            System.out.println("✅ Assignment deleted: " + id);
            return new LoginResponse(true, "Assignment deleted successfully", "admin", null);
        } else {
            return new LoginResponse(false, "Assignment not found", null, null);
        }
    } catch (Exception e) {
        return new LoginResponse(false, "Delete failed: " + e.getMessage(), null, null);
    }
}

// Get assignment statistics
@GetMapping("/assignments/stats")
public Map<String, Long> getAssignmentStats() {
    Map<String, Long> stats = new HashMap<>();
    stats.put("total", assignmentService.getTotalCount());
    stats.put("pending", assignmentService.getCountByStatus("pending"));
    stats.put("in_progress", assignmentService.getCountByStatus("in_progress"));
    stats.put("completed", assignmentService.getCountByStatus("completed"));
    stats.put("canceled", assignmentService.getCountByStatus("canceled"));
    stats.put("not_finished", assignmentService.getCountByStatus("not_finished"));
    stats.put("received", assignmentService.getCountByStatus("received"));
    return stats;
}



// ========== ATTACHMENT DOWNLOAD ENDPOINT ==========

@GetMapping("/messages/download/{filename}")
public ResponseEntity<?> downloadAttachment(@PathVariable String filename) {
    try {
        // Define the upload directory path (create an "uploads" folder in your project root)
        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        Path filePath = Paths.get(uploadDir + filename);
        
        // Check if file exists
        if (!Files.exists(filePath)) {
            // Try alternative path (for demo/development)
            filePath = Paths.get(uploadDir + "attachments/" + filename);
            if (!Files.exists(filePath)) {
                System.out.println("❌ File not found: " + filename);
                return ResponseEntity.notFound().build();
            }
        }
        
        // Read file and return as resource
        Resource resource = new UrlResource(filePath.toUri());
        
        if (resource.exists() && resource.isReadable()) {
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            System.out.println("✅ Downloading file: " + filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } else {
            System.out.println("❌ File not readable: " + filename);
            return ResponseEntity.notFound().build();
        }
    } catch (Exception e) {
        System.err.println("❌ Download error: " + e.getMessage());
        return ResponseEntity.internalServerError().body("Download failed: " + e.getMessage());
    }
}


@GetMapping("/messages")
public List<Message> getAllMessages() {
    return messageService.getAllMessages();
}

@GetMapping("/messages/{id}")
public Optional<Message> getMessageById(@PathVariable String id) {
    return messageService.getMessageById(id);
}

@GetMapping("/messages/employee/{employeeId}")
public List<Message> getMessagesByEmployee(@PathVariable String employeeId) {
    return messageService.getMessagesByEmployee(employeeId);
}

@GetMapping("/messages/unread")
public List<Message> getUnreadMessages() {
    return messageService.getUnreadMessages();
}

@PostMapping("/messages")
public LoginResponse createMessage(@RequestBody Message message) {
    try {
        if (message.getId() == null || message.getId().isEmpty()) {
            long count = messageService.getTotalCount() + 1;
            message.setId(String.format("MSG%03d", count));
        }
        
        Message saved = messageService.createMessage(message);
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", saved.getId());
        data.put("title", saved.getTitle());
        data.put("employeeId", saved.getEmployeeId());
        data.put("status", saved.getStatus());
        
        System.out.println("✅ Message sent: " + saved.getId() + " - " + saved.getTitle());
        return new LoginResponse(true, "Message sent successfully", "admin", data);
    } catch (Exception e) {
        System.out.println("❌ Create message error: " + e.getMessage());
        return new LoginResponse(false, "Send failed: " + e.getMessage(), null, null);
    }
}

@PutMapping("/messages/{id}")
public LoginResponse updateMessage(@PathVariable String id, @RequestBody Message message) {
    try {
        Message updated = messageService.updateMessage(id, message);
        if (updated != null) {
            System.out.println("✅ Message updated: " + id);
            return new LoginResponse(true, "Message updated successfully", "admin", null);
        } else {
            return new LoginResponse(false, "Message not found", null, null);
        }
    } catch (Exception e) {
        return new LoginResponse(false, "Update failed: " + e.getMessage(), null, null);
    }
}

@PutMapping("/messages/{id}/read")
public LoginResponse markMessageAsRead(@PathVariable String id) {
    try {
        boolean updated = messageService.markAsRead(id);
        if (updated) {
            return new LoginResponse(true, "Message marked as read", "admin", null);
        } else {
            return new LoginResponse(false, "Message not found", null, null);
        }
    } catch (Exception e) {
        return new LoginResponse(false, "Update failed: " + e.getMessage(), null, null);
    }
}

@PostMapping("/messages/{id}/reply")
public LoginResponse replyToMessage(@PathVariable String id, @RequestBody Map<String, String> replyData) {
    try {
        String replyContent = replyData.get("reply");
        boolean updated = messageService.addReply(id, replyContent);
        if (updated) {
            System.out.println("✅ Reply sent for message: " + id);
            return new LoginResponse(true, "Reply sent successfully", "admin", null);
        } else {
            return new LoginResponse(false, "Message not found", null, null);
        }
    } catch (Exception e) {
        return new LoginResponse(false, "Reply failed: " + e.getMessage(), null, null);
    }
}

@DeleteMapping("/messages/{id}")
public LoginResponse deleteMessage(@PathVariable String id) {
    try {
        boolean deleted = messageService.deleteMessage(id);
        if (deleted) {
            System.out.println("✅ Message deleted: " + id);
            return new LoginResponse(true, "Message deleted successfully", "admin", null);
        } else {
            return new LoginResponse(false, "Message not found", null, null);
        }
    } catch (Exception e) {
        return new LoginResponse(false, "Delete failed: " + e.getMessage(), null, null);
    }
}

@GetMapping("/messages/stats")
public Map<String, Long> getMessageStats() {
    Map<String, Long> stats = new HashMap<>();
    stats.put("total", messageService.getTotalCount());
    stats.put("unread", messageService.getUnreadCount());
    stats.put("sent", messageService.getCountByStatus("sent"));
    stats.put("delivered", messageService.getCountByStatus("delivered"));
    stats.put("read", messageService.getCountByStatus("read"));
    stats.put("replied", messageService.getCountByStatus("replied"));
    stats.put("pending", messageService.getCountByStatus("pending"));
    return stats;
}


// ========== SETTINGS ENDPOINTS ==========
@GetMapping("/settings/{section}")
public LoginResponse getSettings(@PathVariable String section) {
    try {
        Map<String, Object> settings = settingsService.getSettings(section);
        return new LoginResponse(true, "Settings retrieved", "admin", settings);
    } catch (Exception e) {
        return new LoginResponse(false, "Failed to get settings: " + e.getMessage(), null, null);
    }
}

// Save settings for a section
@PostMapping("/settings/{section}")
public LoginResponse saveSettings(@PathVariable String section, @RequestBody Map<String, Object> settingsData) {
    try {
        String adminName = "Admin"; // Get from session/token
        boolean saved = settingsService.saveSettings(section, settingsData, adminName);
        if (saved) {
            // Apply settings that affect the running application
            applySettings(section, settingsData);
            return new LoginResponse(true, section + " settings saved successfully", "admin", null);
        } else {
            return new LoginResponse(false, "Failed to save settings", null, null);
        }
    } catch (Exception e) {
        return new LoginResponse(false, "Save failed: " + e.getMessage(), null, null);
    }
}

// Update a single setting
@PutMapping("/settings/{section}/{key}")
public LoginResponse updateSetting(@PathVariable String section, @PathVariable String key, @RequestBody Map<String, Object> value) {
    try {
        String adminName = "Admin";
        boolean updated = settingsService.updateSetting(section, key, value.get("value"), adminName);
        if (updated) {
            applySetting(section, key, value.get("value"));
            return new LoginResponse(true, "Setting updated successfully", "admin", null);
        } else {
            return new LoginResponse(false, "Failed to update setting", null, null);
        }
    } catch (Exception e) {
        return new LoginResponse(false, "Update failed: " + e.getMessage(), null, null);
    }
}

// Apply settings to the application
private void applySettings(String section, Map<String, Object> settings) {
    switch(section) {
        case "appearance":
            // Update theme and colors
            String theme = (String) settings.get("theme");
            String primaryColor = (String) settings.get("primaryColor");
            // Store in application context or session
            break;
        case "security":
            Integer sessionTimeout = (Integer) settings.get("sessionTimeout");
            // Update session timeout configuration
            break;
        case "system":
            Boolean maintenanceMode = (Boolean) settings.get("maintenanceMode");
            // Enable/disable maintenance mode
            break;
        case "notifications":
            // Update notification settings
            break;
    }
}

private void applySetting(String section, String key, Object value) {
    // Apply individual setting change
    if ("appearance".equals(section) && "theme".equals(key)) {
        // Update theme
    } else if ("system".equals(section) && "maintenanceMode".equals(key)) {
        // Update maintenance mode
    }
}

// Export data
@GetMapping("/export/{type}")
public ResponseEntity<?> exportData(@PathVariable String type) {
    try {
        Map<String, Object> exportData = new HashMap<>();
        
        switch(type) {
            case "employees":
                exportData.put("employees", employeeService.getAllEmployees());
                exportData.put("count", employeeService.getTotalEmployeeCount());
                exportData.put("exportDate", LocalDateTime.now().toString());
                break;
            case "assignments":
                exportData.put("assignments", assignmentService.getAllAssignments());
                exportData.put("count", assignmentService.getTotalCount());
                break;
            case "messages":
                exportData.put("messages", messageService.getAllMessages());
                exportData.put("count", messageService.getTotalCount());
                break;
            case "all":
                exportData.put("employees", employeeService.getAllEmployees());
                exportData.put("admins", adminService.getAllAdmins());
                exportData.put("assignments", assignmentService.getAllAssignments());
                exportData.put("messages", messageService.getAllMessages());
                exportData.put("exportDate", LocalDateTime.now().toString());
                break;
            default:
                return ResponseEntity.badRequest().body("Invalid export type");
        }
        
        return ResponseEntity.ok(exportData);
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("Export failed: " + e.getMessage());
    }
}

// Get system status
@GetMapping("/system/status")
public Map<String, Object> getSystemStatus() {
    Map<String, Object> status = new HashMap<>();
    status.put("apiStatus", "Connected");
    status.put("database", "PostgreSQL Active");
    status.put("serverLoad", "Normal");
    status.put("version", "2.0.0");
    status.put("uptime", System.currentTimeMillis());
    status.put("timestamp", LocalDateTime.now().toString());
    status.put("maintenanceMode", settingsService.getSetting("system", "maintenanceMode", Boolean.class));
    return status;
}

// Clear all settings (reset to default)
@DeleteMapping("/settings/{section}")
public LoginResponse resetSettings(@PathVariable String section) {
    try {
        // Reset to default settings
        Map<String, Object> defaultSettings = getDefaultSettings(section);
        String adminName = "Admin";
        settingsService.saveSettings(section, defaultSettings, adminName);
        return new LoginResponse(true, "Settings reset to default", "admin", null);
    } catch (Exception e) {
        return new LoginResponse(false, "Reset failed: " + e.getMessage(), null, null);
    }
}

private Map<String, Object> getDefaultSettings(String section) {
    Map<String, Object> defaults = new HashMap<>();
    switch(section) {
        case "general":
            defaults.put("companyName", "Traployee");
            defaults.put("companyEmail", "contact@traployee.com");
            defaults.put("companyPhone", "+1 234 567 8900");
            defaults.put("timezone", "America/New_York");
            defaults.put("dateFormat", "YYYY-MM-DD");
            defaults.put("language", "en");
            break;
        case "appearance":
            defaults.put("theme", "dark");
            defaults.put("primaryColor", "#c8f55a");
            defaults.put("sidebarCollapsed", false);
            defaults.put("animations", true);
            defaults.put("compactView", false);
            defaults.put("fontSize", "medium");
            break;
        case "security":
            defaults.put("sessionTimeout", 60);
            defaults.put("maxLoginAttempts", 5);
            defaults.put("passwordExpiryDays", 90);
            defaults.put("requireStrongPassword", true);
            defaults.put("ipWhitelist", "[]");
            defaults.put("allowedDomains", "[\"traployee.com\"]");
            break;
        // Add other sections...
    }
    return defaults;
}



// ========== MESSAGE WITH ATTACHMENT ENDPOINTS ==========

@PostMapping(value = "/messages/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public LoginResponse createMessageWithAttachment(
        @RequestPart("message") String messageJson,
        @RequestPart(value = "file", required = false) MultipartFile file) {
    try {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        Message message = mapper.readValue(messageJson, Message.class);
        
        if (message.getId() == null || message.getId().isEmpty()) {
            long count = messageService.getTotalCount() + 1;
            message.setId(String.format("MSG%03d", count));
        }
        
        // Save the file if present
        if (file != null && !file.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename to avoid conflicts
            String timestamp = String.valueOf(System.currentTimeMillis());
            String originalFilename = file.getOriginalFilename();
            String fileName = timestamp + "_" + originalFilename;
            
            java.nio.file.Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            message.setAttachment(fileName);
            
            // Determine attachment type
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            String attachmentType = "other";
            if (ext.matches("jpg|jpeg|png|gif|webp")) attachmentType = "image";
            else if (ext.equals("pdf")) attachmentType = "pdf";
            else if (ext.matches("doc|docx")) attachmentType = "doc";
            else if (ext.matches("xls|xlsx")) attachmentType = "excel";
            message.setAttachmentType(attachmentType);
        }
        
        Message saved = messageService.createMessage(message);
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", saved.getId());
        data.put("title", saved.getTitle());
        data.put("employeeId", saved.getEmployeeId());
        data.put("status", saved.getStatus());
        
        System.out.println("✅ Message sent: " + saved.getId() + " - " + saved.getTitle());
        if (file != null) {
            System.out.println("   📎 Attachment saved: " + file.getOriginalFilename());
        }
        return new LoginResponse(true, "Message sent successfully", "admin", data);
    } catch (Exception e) {
        System.err.println("❌ Create message error: " + e.getMessage());
        e.printStackTrace();
        return new LoginResponse(false, "Send failed: " + e.getMessage(), null, null);
    }
}

@PutMapping(value = "/messages/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public LoginResponse updateMessageWithAttachment(
        @PathVariable String id,
        @RequestPart("message") String messageJson,
        @RequestPart(value = "file", required = false) MultipartFile file) {
    try {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        Message message = mapper.readValue(messageJson, Message.class);
        
        // Save new file if present
        if (file != null && !file.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String timestamp = String.valueOf(System.currentTimeMillis());
            String originalFilename = file.getOriginalFilename();
            String fileName = timestamp + "_" + originalFilename;
            
            java.nio.file.Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            message.setAttachment(fileName);
            
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            String attachmentType = "other";
            if (ext.matches("jpg|jpeg|png|gif|webp")) attachmentType = "image";
            else if (ext.equals("pdf")) attachmentType = "pdf";
            else if (ext.matches("doc|docx")) attachmentType = "doc";
            else if (ext.matches("xls|xlsx")) attachmentType = "excel";
            message.setAttachmentType(attachmentType);
        }
        
        Message updated = messageService.updateMessage(id, message);
        if (updated != null) {
            System.out.println("✅ Message updated: " + id);
            if (file != null) {
                System.out.println("   📎 Attachment saved: " + file.getOriginalFilename());
            }
            return new LoginResponse(true, "Message updated successfully", "admin", null);
        } else {
            return new LoginResponse(false, "Message not found", null, null);
        }
    } catch (Exception e) {
        System.err.println("❌ Update message error: " + e.getMessage());
        return new LoginResponse(false, "Update failed: " + e.getMessage(), null, null);
    }
}


// ========== ASSIGNMENT REPLY & SUBMISSION ENDPOINTS ==========

// Employee reply to assignment
@PostMapping("/assignments/{id}/reply")
public LoginResponse employeeReplyToAssignment(@PathVariable String id, @RequestBody Map<String, String> replyData) {
    try {
        String reply = replyData.get("reply");
        boolean updated = assignmentService.addEmployeeReply(id, reply);
        if (updated) {
            System.out.println("✅ Employee reply sent for assignment: " + id);
            return new LoginResponse(true, "Reply sent successfully", "employee", null);
        } else {
            return new LoginResponse(false, "Assignment not found", null, null);
        }
    } catch (Exception e) {
        System.err.println("❌ Reply error: " + e.getMessage());
        return new LoginResponse(false, "Reply failed: " + e.getMessage(), null, null);
    }
}

// Admin reply to assignment submission
@PostMapping("/assignments/{id}/admin-reply")
public LoginResponse adminReplyToAssignment(@PathVariable String id, @RequestBody Map<String, String> replyData) {
    try {
        String reply = replyData.get("reply");
        String status = replyData.get("status");
        boolean updated = assignmentService.addAdminReply(id, reply, status);
        if (updated) {
            System.out.println("✅ Admin reply sent for assignment: " + id);
            return new LoginResponse(true, "Feedback sent successfully", "admin", null);
        } else {
            return new LoginResponse(false, "Assignment not found", null, null);
        }
    } catch (Exception e) {
        System.err.println("❌ Admin reply error: " + e.getMessage());
        return new LoginResponse(false, "Reply failed: " + e.getMessage(), null, null);
    }
}

// Submit assignment with file
@PostMapping(value = "/assignments/{id}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public LoginResponse submitAssignment(
        @PathVariable String id,
        @RequestPart(value = "file", required = false) MultipartFile file,
        @RequestPart(value = "submissionNote", required = false) String submissionNote,
        @RequestPart(value = "status", required = false) String status) {
    try {
        // Save the file if present
        String fileName = null;
        if (file != null && !file.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/uploads/assignments/";
            java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String timestamp = String.valueOf(System.currentTimeMillis());
            String originalFilename = file.getOriginalFilename();
            fileName = timestamp + "_" + originalFilename;
            
            java.nio.file.Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        
        boolean updated = assignmentService.submitAssignment(id, fileName, submissionNote);
        if (updated) {
            System.out.println("✅ Assignment submitted: " + id);
            return new LoginResponse(true, "Assignment submitted successfully", "employee", null);
        } else {
            return new LoginResponse(false, "Assignment not found", null, null);
        }
    } catch (Exception e) {
        System.err.println("❌ Submission error: " + e.getMessage());
        return new LoginResponse(false, "Submission failed: " + e.getMessage(), null, null);
    }
}

// Get all submitted assignments (for reports)
@GetMapping("/assignments/submissions")
public List<Assignment> getAllSubmissions() {
    List<Assignment> allAssignments = assignmentService.getAllAssignments();
    // Filter only assignments that have been submitted (have submittedFile or status completed)
    return allAssignments.stream()
            .filter(a -> a.getSubmittedFile() != null || "completed".equals(a.getStatus()))
            .collect(java.util.stream.Collectors.toList());
}

// Download assignment submission file
@GetMapping("/assignments/download/{filename}")
public ResponseEntity<?> downloadAssignmentFile(@PathVariable String filename) {
    try {
        String uploadDir = System.getProperty("user.dir") + "/uploads/assignments/";
        Path filePath = Paths.get(uploadDir + filename);
        
        if (!Files.exists(filePath)) {
            System.out.println("❌ File not found: " + filename);
            return ResponseEntity.notFound().build();
        }
        
        Resource resource = new UrlResource(filePath.toUri());
        
        if (resource.exists() && resource.isReadable()) {
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            System.out.println("✅ Downloading assignment file: " + filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename.substring(filename.indexOf("_") + 1) + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    } catch (Exception e) {
        System.err.println("❌ Download error: " + e.getMessage());
        return ResponseEntity.internalServerError().body("Download failed: " + e.getMessage());
    }
}



// Get all sessions
@GetMapping("/sessions")
public List<Session> getAllSessions() {
    return sessionService.getAllSessions();
}

// Get sessions by employee
@GetMapping("/sessions/employee/{employeeId}")
public List<Session> getSessionsByEmployee(@PathVariable String employeeId) {
    return sessionService.getSessionsByEmployee(employeeId);
}

// Get sessions by assignment
@GetMapping("/sessions/assignment/{assignmentId}")
public List<Session> getSessionsByAssignment(@PathVariable String assignmentId) {
    return sessionService.getSessionsByAssignment(assignmentId);
}

// Get full analytics for an employee (keyboard, mouse, screenshots)
@GetMapping("/analytics/employee/{employeeId}")
public Map<String, Object> getEmployeeAnalytics(@PathVariable String employeeId) {
    return analyticsService.getEmployeeAnalytics(employeeId);
}

// Get full analytics for an assignment
@GetMapping("/analytics/assignment/{assignmentId}")
public Map<String, Object> getAssignmentAnalytics(@PathVariable String assignmentId) {
    return analyticsService.getAssignmentAnalytics(assignmentId);
}

// Get all analytics summary
@GetMapping("/analytics/summary")
public Map<String, Object> getAllAnalytics() {
    return analyticsService.getAllAnalytics();
}

// Download screenshot file - FIXED PATH with better matching
@GetMapping("/screenshots/download/{filename}")
public ResponseEntity<?> downloadScreenshot(@PathVariable String filename) {
    try {
        String userDir = System.getProperty("user.dir");
        
        // Define the possible base directories
        String[] baseDirs = {
            userDir + "/Workflow/Screenshots/",
            userDir + "/../trackers/Workflow/Screenshots/",
            userDir + "/../../trackers/Workflow/Screenshots/",
            "C:/Users/samij/Traployee/trackers/Workflow/Screenshots/",
            userDir + "/uploads/screenshots/",
        };
        
        Path filePath = null;
        
        // First try exact match
        for (String baseDir : baseDirs) {
            Path testPath = Paths.get(baseDir + filename);
            System.out.println("Checking: " + testPath.toString());
            if (Files.exists(testPath)) {
                filePath = testPath;
                System.out.println("✅ Found screenshot at: " + filePath.toString());
                break;
            }
        }
        
        // If not found, try to search for any file containing the employee ID
        if (filePath == null) {
            // Extract employee ID from filename if present
            // Or just search the directory for matching files
            for (String baseDir : baseDirs) {
                Path dirPath = Paths.get(baseDir);
                if (Files.exists(dirPath)) {
                    try (var stream = Files.list(dirPath)) {
                        var matchingFile = stream
                            .filter(path -> path.getFileName().toString().contains(filename) || 
                                           filename.contains(path.getFileName().toString().split("_")[0]))
                            .findFirst();
                        if (matchingFile.isPresent()) {
                            filePath = matchingFile.get();
                            System.out.println("✅ Found matching screenshot: " + filePath.toString());
                            break;
                        }
                    }
                }
            }
        }
        
        if (filePath == null || !Files.exists(filePath)) {
            System.out.println("❌ Screenshot not found: " + filename);
            // Return a placeholder image instead of 404
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(new UrlResource(Paths.get("C:/Users/samij/Traployee/traployee-backend/src/main/resources/static/placeholder.png").toUri()));
        }
        
        Resource resource = new UrlResource(filePath.toUri());
        
        if (resource.exists() && resource.isReadable()) {
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "image/png";
            }
            
            System.out.println("✅ Downloading screenshot: " + filePath.getFileName());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    } catch (Exception e) {
        System.err.println("❌ Download error: " + e.getMessage());
        return ResponseEntity.internalServerError().body("Download failed: " + e.getMessage());
    }
}
// Get screenshots by employee
@GetMapping("/screenshots/employee/{employeeId}")
public List<Screenshot> getScreenshotsByEmployee(@PathVariable String employeeId) {
    return screenshotRepository.findByEmployeeId(employeeId);
}

@GetMapping("/screenshots/{filename}")
public ResponseEntity<Resource> serveScreenshot(@PathVariable String filename) {
    try {
        Path filePath = Paths.get(System.getProperty("user.dir") + "/Workflow/Screenshots/").resolve(filename);
        Resource resource = new UrlResource(filePath.toUri());
        
        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    } catch (Exception e) {
        return ResponseEntity.notFound().build();
    }
}

// List screenshots for an employee by scanning the directory
@GetMapping("/screenshots/list/{employeeId}")
public List<Map<String, Object>> listScreenshotsByEmployee(@PathVariable String employeeId) {
    List<Map<String, Object>> screenshots = new ArrayList<>();
    
    String[] baseDirs = {
        "C:/Users/samij/Traployee/trackers/Workflow/Screenshots/",
        System.getProperty("user.dir") + "/Workflow/Screenshots/",
        System.getProperty("user.dir") + "/../trackers/Workflow/Screenshots/",
    };
    
    for (String baseDir : baseDirs) {
        Path dirPath = Paths.get(baseDir);
        if (Files.exists(dirPath)) {
            try (var stream = Files.list(dirPath)) {
                var matchingFiles = stream
                    .filter(path -> path.getFileName().toString().contains(employeeId))
                    .toList();
                
                for (Path filePath : matchingFiles) {
                    Map<String, Object> screenshot = new HashMap<>();
                    screenshot.put("filename", filePath.getFileName().toString());
                    screenshot.put("filepath", filePath.toString());
                    screenshot.put("screenshotTime", Files.getLastModifiedTime(filePath).toMillis());
                    screenshots.add(screenshot);
                }
                
                if (!screenshots.isEmpty()) {
                    break;
                }
            } catch (Exception e) {
                System.err.println("Error listing screenshots: " + e.getMessage());
            }
        }
    }
    
    return screenshots;
}

}