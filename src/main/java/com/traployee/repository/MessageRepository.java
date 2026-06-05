package com.traployee.repository;

import com.traployee.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    
    List<Message> findByEmployeeId(String employeeId);
    
    List<Message> findByStatus(String status);
    
    List<Message> findByIsRead(boolean isRead);
    
    long countByIsRead(boolean isRead);
    
    long countByStatus(String status);
    
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.id = :id")
    int markAsRead(@Param("id") String id);
    
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.status = :status, m.replyContent = :replyContent, m.repliedAt = CURRENT_TIMESTAMP WHERE m.id = :id")
    int addReply(@Param("id") String id, @Param("status") String status, @Param("replyContent") String replyContent);
}