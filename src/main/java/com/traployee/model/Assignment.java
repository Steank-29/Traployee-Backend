package com.traployee.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
public class Assignment {
    
    @Id
    @Column(length = 50)
    private String id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "employee_id", nullable = false, length = 50)
    private String employeeId;
    
    @Column(name = "employee_name", length = 100)
    private String employeeName;
    
    @Column(name = "due_date")
    private String dueDate;
    
    @Column(length = 50)
    private String status = "pending";
    
    // Submission fields
    @Column(name = "submitted_file", length = 500)
    private String submittedFile;
    
    @Column(name = "submission_note", columnDefinition = "TEXT")
    private String submissionNote;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    // Reply fields
    @Column(name = "employee_reply", columnDefinition = "TEXT")
    private String employeeReply;
    
    @Column(name = "employee_replied_at")
    private LocalDateTime employeeRepliedAt;
    
    @Column(name = "admin_reply", columnDefinition = "TEXT")
    private String adminReply;
    
    @Column(name = "admin_replied_at")
    private LocalDateTime adminRepliedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getSubmittedFile() { return submittedFile; }
    public void setSubmittedFile(String submittedFile) { this.submittedFile = submittedFile; }
    
    public String getSubmissionNote() { return submissionNote; }
    public void setSubmissionNote(String submissionNote) { this.submissionNote = submissionNote; }
    
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    
    public String getEmployeeReply() { return employeeReply; }
    public void setEmployeeReply(String employeeReply) { this.employeeReply = employeeReply; }
    
    public LocalDateTime getEmployeeRepliedAt() { return employeeRepliedAt; }
    public void setEmployeeRepliedAt(LocalDateTime employeeRepliedAt) { this.employeeRepliedAt = employeeRepliedAt; }
    
    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }
    
    public LocalDateTime getAdminRepliedAt() { return adminRepliedAt; }
    public void setAdminRepliedAt(LocalDateTime adminRepliedAt) { this.adminRepliedAt = adminRepliedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}