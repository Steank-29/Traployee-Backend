package com.traployee.service;

import com.traployee.model.Session;
import com.traployee.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SessionService {
    
    @Autowired
    private SessionRepository sessionRepository;
    
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }
    
    public Optional<Session> getSessionById(String sessionId) {
        return sessionRepository.findById(sessionId);
    }
    
    public List<Session> getSessionsByEmployee(String employeeId) {
        return sessionRepository.findByEmployeeId(employeeId);
    }
    
    public List<Session> getSessionsByAssignment(String assignmentId) {
        return sessionRepository.findByAssignmentId(assignmentId);
    }
}