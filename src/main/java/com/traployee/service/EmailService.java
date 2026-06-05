package com.traployee.service;

import com.traployee.model.Assignment;
import com.traployee.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendAssignmentNotification(Assignment assignment, Employee employee) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(employee.getEmail());
            helper.setSubject("📋 New Assignment: " + assignment.getTitle());
            
            // Create email content with Thymeleaf
            Context context = new Context();
            context.setVariable("assignment", assignment);
            context.setVariable("employee", employee);
            context.setVariable("companyName", "Traployee");
            context.setVariable("year", java.time.Year.now().getValue());
            
            String htmlContent = templateEngine.process("assignment-notification", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            System.out.println("✅ Assignment notification email sent to: " + employee.getEmail());
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void sendAssignmentStatusUpdate(Assignment assignment, Employee employee, String oldStatus, String newStatus) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(employee.getEmail());
            helper.setSubject("📝 Assignment Status Update: " + assignment.getTitle());
            
            Context context = new Context();
            context.setVariable("assignment", assignment);
            context.setVariable("employee", employee);
            context.setVariable("oldStatus", oldStatus);
            context.setVariable("newStatus", newStatus);
            context.setVariable("companyName", "Traployee");
            context.setVariable("year", java.time.Year.now().getValue());
            
            String htmlContent = templateEngine.process("assignment-status-update", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            System.out.println("✅ Status update email sent to: " + employee.getEmail());
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send status email: " + e.getMessage());
        }
    }

    public void sendAdminReplyNotification(Assignment assignment, Employee employee, String reply, String newStatus) {
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(employee.getEmail());
        helper.setSubject("📨 Admin Feedback on Assignment: " + assignment.getTitle());
        
        Context context = new Context();
        context.setVariable("assignment", assignment);
        context.setVariable("employee", employee);
        context.setVariable("adminReply", reply);
        context.setVariable("newStatus", newStatus);
        context.setVariable("companyName", "Traployee");
        context.setVariable("year", java.time.Year.now().getValue());
        
        String htmlContent = templateEngine.process("admin-reply-notification", context);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
        System.out.println("✅ Admin reply email sent to: " + employee.getEmail());
        
    } catch (Exception e) {
        System.err.println("❌ Failed to send admin reply email: " + e.getMessage());
    }
}
}