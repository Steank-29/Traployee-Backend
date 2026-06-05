package com.traployee.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mouse_activity")
public class MouseActivity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    @Column(name = "employee_id", length = 50)
    private String employeeId;
    
    @Column(name = "action_type", length = 20)
    private String actionType;
    
    private Integer button;
    
    @Column(name = "x_position")
    private Integer xPosition;
    
    @Column(name = "y_position")
    private Integer yPosition;
    
    private LocalDateTime timestamp;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    
    public Integer getButton() { return button; }
    public void setButton(Integer button) { this.button = button; }
    
    public Integer getXPosition() { return xPosition; }
    public void setXPosition(Integer xPosition) { this.xPosition = xPosition; }
    
    public Integer getYPosition() { return yPosition; }
    public void setYPosition(Integer yPosition) { this.yPosition = yPosition; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}