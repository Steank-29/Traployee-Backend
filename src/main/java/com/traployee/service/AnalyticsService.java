package com.traployee.service;

import com.traployee.model.*;
import com.traployee.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private KeyboardActivityRepository keyboardActivityRepository;
    
    @Autowired
    private MouseActivityRepository mouseActivityRepository;
    
    
    @Autowired
    private ScreenshotRepository screenshotRepository;
    
    public Map<String, Object> getEmployeeAnalytics(String employeeId) {
        Map<String, Object> analytics = new HashMap<>();
        
        List<Session> sessions = sessionRepository.findByEmployeeId(employeeId);
        List<KeyboardActivity> keyboardActivities = keyboardActivityRepository.findByEmployeeId(employeeId);
        List<MouseActivity> mouseActivities = mouseActivityRepository.findByEmployeeId(employeeId);
        List<Screenshot> screenshots = screenshotRepository.findByEmployeeId(employeeId);
        
        analytics.put("sessions", sessions);
        analytics.put("keyboardActivities", keyboardActivities);
        analytics.put("mouseActivities", mouseActivities);
        analytics.put("screenshots", screenshots);
        
        // Calculate totals
        int totalKeystrokes = keyboardActivities.size();
        int totalMouseClicks = (int) mouseActivities.stream().filter(m -> "click".equals(m.getActionType())).count();
        int totalMouseMovements = (int) mouseActivities.stream().filter(m -> "move".equals(m.getActionType())).count();
        
        analytics.put("totalKeystrokes", totalKeystrokes);
        analytics.put("totalMouseClicks", totalMouseClicks);
        analytics.put("totalMouseMovements", totalMouseMovements);
        analytics.put("totalScreenshots", screenshots.size());
        
        return analytics;
    }
    
    public Map<String, Object> getAssignmentAnalytics(String assignmentId) {
        Map<String, Object> analytics = new HashMap<>();
        
        List<Session> sessions = sessionRepository.findByAssignmentId(assignmentId);
        analytics.put("sessions", sessions);
        
        // Get all unique employees who worked on this assignment
        List<String> employeeIds = sessions.stream()
                .map(Session::getEmployeeId)
                .distinct()
                .toList();
        analytics.put("employeeIds", employeeIds);
        
        return analytics;
    }
    
    public Map<String, Object> getAllAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        List<Session> allSessions = sessionRepository.findAll();
        List<KeyboardActivity> allKeyboard = keyboardActivityRepository.findAll();
        List<MouseActivity> allMouse = mouseActivityRepository.findAll();
        List<Screenshot> allScreenshots = screenshotRepository.findAll();
        
        analytics.put("totalSessions", allSessions.size());
        analytics.put("totalKeystrokes", allKeyboard.size());
        analytics.put("totalMouseClicks", allMouse.stream().filter(m -> "click".equals(m.getActionType())).count());
        analytics.put("totalMouseMovements", allMouse.stream().filter(m -> "move".equals(m.getActionType())).count());
        analytics.put("totalScreenshots", allScreenshots.size());
        analytics.put("sessions", allSessions);
        
        return analytics;
    }
}