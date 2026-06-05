package com.traployee.repository;

import com.traployee.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    
    List<Assignment> findByEmployeeId(String employeeId);
    
    List<Assignment> findByStatus(String status);
    
    long countByStatus(String status);
    
    @Modifying
    @Transactional
    @Query("UPDATE Assignment a SET a.status = :status WHERE a.id = :id")
    int updateStatus(@Param("id") String id, @Param("status") String status);
    
    @Modifying
    @Transactional
    @Query("UPDATE Assignment a SET a.employeeReply = :reply, a.employeeRepliedAt = CURRENT_TIMESTAMP WHERE a.id = :id")
    int addEmployeeReply(@Param("id") String id, @Param("reply") String reply);
    
    @Modifying
    @Transactional
    @Query("UPDATE Assignment a SET a.adminReply = :reply, a.adminRepliedAt = CURRENT_TIMESTAMP, a.status = :status WHERE a.id = :id")
    int addAdminReply(@Param("id") String id, @Param("reply") String reply, @Param("status") String status);
    
    @Modifying
    @Transactional
    @Query("UPDATE Assignment a SET a.submittedFile = :fileName, a.submissionNote = :submissionNote, a.submittedAt = CURRENT_TIMESTAMP, a.status = 'completed' WHERE a.id = :id")
    int submitAssignment(@Param("id") String id, @Param("fileName") String fileName, @Param("submissionNote") String submissionNote);
}