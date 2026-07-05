package com.ems.repository;

import com.ems.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Salary entity.
 */
@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    Optional<Salary> findByEmployeeEmpId(Long empId);
    void deleteByEmployeeEmpId(Long empId);
}
