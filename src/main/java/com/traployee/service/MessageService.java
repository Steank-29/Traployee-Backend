package com.traployee.service;

import com.traployee.model.Message;
import com.traployee.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }
    
    public Optional<Message> getMessageById(String id) {
        return messageRepository.findById(id);
    }
    
    public List<Message> getMessagesByEmployee(String employeeId) {
        return messageRepository.findByEmployeeId(employeeId);
    }
    
    public List<Message> getMessagesByStatus(String status) {
        return messageRepository.findByStatus(status);
    }
    
    public List<Message> getUnreadMessages() {
        return messageRepository.findByIsRead(false);
    }
    
    public Message createMessage(Message message) {
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        message.setRead(false);
        if (message.getStatus() == null) {
            message.setStatus("sent");
        }
        if (message.getSentDate() == null) {
            message.setSentDate(LocalDateTime.now().toLocalDate().toString());
        }
        return messageRepository.save(message);
    }
    
    public Message updateMessage(String id, Message messageDetails) {
        Optional<Message> existingOpt = messageRepository.findById(id);
        if (existingOpt.isPresent()) {
            Message existing = existingOpt.get();
            existing.setEmployeeId(messageDetails.getEmployeeId());
            existing.setEmployeeName(messageDetails.getEmployeeName());
            existing.setTitle(messageDetails.getTitle());
            existing.setSubject(messageDetails.getSubject());
            existing.setContent(messageDetails.getContent());
            existing.setAttachment(messageDetails.getAttachment());
            existing.setAttachmentType(messageDetails.getAttachmentType());
            existing.setReplyByDate(messageDetails.getReplyByDate());
            existing.setSentDate(messageDetails.getSentDate());
            existing.setStatus(messageDetails.getStatus());
            existing.setUpdatedAt(LocalDateTime.now());
            return messageRepository.save(existing);
        }
        return null;
    }
    
    @Transactional
    public boolean markAsRead(String id) {
        int updated = messageRepository.markAsRead(id);
        return updated > 0;
    }
    
    @Transactional
    public boolean addReply(String id, String replyContent) {
        int updated = messageRepository.addReply(id, "replied", replyContent);
        return updated > 0;
    }
    
    @Transactional
    public boolean deleteMessage(String id) {
        if (messageRepository.existsById(id)) {
            messageRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public long getTotalCount() {
        return messageRepository.count();
    }
    
    public long getUnreadCount() {
        return messageRepository.countByIsRead(false);
    }
    
    public long getCountByStatus(String status) {
        return messageRepository.countByStatus(status);
    }
}