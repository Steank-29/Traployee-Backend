package com.traployee.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class Session {
    
    @Id
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    @Column(name = "employee_id", length = 50)
    private String employeeId;
    
    @Column(name = "assignment_id", length = 50)
    private String assignmentId;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    private String status;
    
    @Column(name = "total_duration")
    private Long totalDuration;
    
    @Column(name = "active_time")
    private Long activeTime;
    
    @Column(name = "total_pause_duration")
    private Long totalPauseDuration;
    
    @Column(name = "screenshots_taken")
    private Integer screenshotsTaken;
    
    @Column(name = "total_keystrokes")
    private Integer totalKeystrokes;
    
    @Column(name = "total_mouse_clicks")
    private Integer totalMouseClicks;
    
    @Column(name = "total_mouse_movements")
    private Integer totalMouseMovements;
    
    @Column(name = "total_pauses")
    private Integer totalPauses;
    
    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Long getTotalDuration() { return totalDuration; }
    public void setTotalDuration(Long totalDuration) { this.totalDuration = totalDuration; }
    
    public Long getActiveTime() { return activeTime; }
    public void setActiveTime(Long activeTime) { this.activeTime = activeTime; }
    
    public Long getTotalPauseDuration() { return totalPauseDuration; }
    public void setTotalPauseDuration(Long totalPauseDuration) { this.totalPauseDuration = totalPauseDuration; }
    
    public Integer getScreenshotsTaken() { return screenshotsTaken; }
    public void setScreenshotsTaken(Integer screenshotsTaken) { this.screenshotsTaken = screenshotsTaken; }
    
    public Integer getTotalKeystrokes() { return totalKeystrokes; }
    public void setTotalKeystrokes(Integer totalKeystrokes) { this.totalKeystrokes = totalKeystrokes; }
    
    public Integer getTotalMouseClicks() { return totalMouseClicks; }
    public void setTotalMouseClicks(Integer totalMouseClicks) { this.totalMouseClicks = totalMouseClicks; }
    
    public Integer getTotalMouseMovements() { return totalMouseMovements; }
    public void setTotalMouseMovements(Integer totalMouseMovements) { this.totalMouseMovements = totalMouseMovements; }
    
    public Integer getTotalPauses() { return totalPauses; }
    public void setTotalPauses(Integer totalPauses) { this.totalPauses = totalPauses; }
}