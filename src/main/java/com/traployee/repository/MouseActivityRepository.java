package com.traployee.repository;

import com.traployee.model.MouseActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MouseActivityRepository extends JpaRepository<MouseActivity, Long> {
    List<MouseActivity> findBySessionId(String sessionId);
    List<MouseActivity> findByEmployeeId(String employeeId);
}