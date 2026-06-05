package com.traployee.dto;

import java.util.Map;

public class LoginResponse {
    private boolean success;
    private String message;
    private String userType;
    private Map<String, Object> data;
    
    public LoginResponse() {}
    
    public LoginResponse(boolean success, String message, String userType, Map<String, Object> data) {
        this.success = success;
        this.message = message;
        this.userType = userType;
        this.data = data;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}