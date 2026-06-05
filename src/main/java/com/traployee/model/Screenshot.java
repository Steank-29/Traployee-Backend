package com.traployee.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "screenshots")
public class Screenshot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    @Column(name = "employee_id", length = 50)
    private String employeeId;
    
    private String filename;
    private String filepath;
    
    @Column(name = "screenshot_time")
    private LocalDateTime screenshotTime;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    
    public String getFilepath() { return filepath; }
    public void setFilepath(String filepath) { this.filepath = filepath; }
    
    public LocalDateTime getScreenshotTime() { return screenshotTime; }
    public void setScreenshotTime(LocalDateTime screenshotTime) { this.screenshotTime = screenshotTime; }
}