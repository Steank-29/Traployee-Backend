package com.traployee.repository;

import com.traployee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    // Find employee by ID and Name (for login)
    Optional<Employee> findByIdAndName(String id, String name);
    
    // Check if employee exists by ID
    boolean existsById(String id);
    
    // Count active employees
    long countByStatus(String status);
    
    // Force delete by ID - returns int to confirm deletion
    @Modifying
    @Transactional
    @Query("DELETE FROM Employee e WHERE e.id = :id")
    int deleteEmployeeById(@Param("id") String id);
}