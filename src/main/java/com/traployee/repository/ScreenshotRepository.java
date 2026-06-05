package com.traployee.repository;

import com.traployee.model.Screenshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScreenshotRepository extends JpaRepository<Screenshot, Long> {
    List<Screenshot> findBySessionId(String sessionId);
    List<Screenshot> findByEmployeeId(String employeeId);
}