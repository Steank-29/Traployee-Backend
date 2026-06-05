package com.traployee.repository;

import com.traployee.model.KeyboardActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KeyboardActivityRepository extends JpaRepository<KeyboardActivity, Long> {
    List<KeyboardActivity> findBySessionId(String sessionId);
    List<KeyboardActivity> findByEmployeeId(String employeeId);
}