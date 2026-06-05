package com.traployee.repository;

import com.traployee.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    List<Session> findByEmployeeId(String employeeId);
    List<Session> findByAssignmentId(String assignmentId);
    
}