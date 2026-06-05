package com.traployee.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "keyboard_activity")
public class KeyboardActivity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    @Column(name = "employee_id", length = 50)
    private String employeeId;
    
    @Column(name = "key_pressed", length = 50)
    private String keyPressed;
    
    @Column(name = "key_code", length = 50)
    private String keyCode;
    
    private LocalDateTime timestamp;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getKeyPressed() { return keyPressed; }
    public void setKeyPressed(String keyPressed) { this.keyPressed = keyPressed; }
    
    public String getKeyCode() { return keyCode; }
    public void setKeyCode(String keyCode) { this.keyCode = keyCode; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}