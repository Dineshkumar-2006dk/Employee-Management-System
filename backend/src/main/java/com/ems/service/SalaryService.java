package com.ems.service;

import com.ems.dto.SalaryDTO;
import com.ems.entity.Employee;
import com.ems.entity.Salary;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class handling logic for Salary Management.
 */
@Service
public class SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Add or set salary details for an employee.
     */
    @Transactional
    public SalaryDTO saveSalary(SalaryDTO dto) {
        if (dto.getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required.");
        }
        if (dto.getBasicSalary() == null || dto.getBasicSalary() < 0) {
            throw new IllegalArgumentException("Basic salary must be a positive number.");
        }
        if (dto.getBonus() == null || dto.getBonus() < 0) {
            throw new IllegalArgumentException("Bonus must be a positive number.");
        }

        Employee employee = employeeRepository.findById(dto.getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + dto.getEmpId()));

        // Check if salary record already exists for this employee, if so we update it
        Optional<Salary> existingSalary = salaryRepository.findByEmployeeEmpId(dto.getEmpId());
        Salary salary;
        if (existingSalary.isPresent()) {
            salary = existingSalary.get();
            salary.setBasicSalary(dto.getBasicSalary());
            salary.setBonus(dto.getBonus());
        } else {
            salary = new Salary(employee, dto.getBasicSalary(), dto.getBonus());
        }

        salary.calculateTotalSalary();
        Salary saved = salaryRepository.save(salary);
        return convertToDTO(saved);
    }

    /**
     * Updates an existing salary record by its salary ID.
     */
    @Transactional
    public SalaryDTO updateSalary(Long id, SalaryDTO dto) {
        Salary salary = salaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary record not found with ID: " + id));

        if (dto.getBasicSalary() == null || dto.getBasicSalary() < 0) {
            throw new IllegalArgumentException("Basic salary must be a positive number.");
        }
        if (dto.getBonus() == null || dto.getBonus() < 0) {
            throw new IllegalArgumentException("Bonus must be a positive number.");
        }

        salary.setBasicSalary(dto.getBasicSalary());
        salary.setBonus(dto.getBonus());
        salary.calculateTotalSalary();

        Salary updated = salaryRepository.save(salary);
        return convertToDTO(updated);
    }

    /**
     * Retrieve salary record for a specific employee.
     */
    @Transactional(readOnly = true)
    public SalaryDTO getSalaryByEmployeeId(Long empId) {
        Salary salary = salaryRepository.findByEmployeeEmpId(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Salary details not found for Employee ID: " + empId));
        return convertToDTO(salary);
    }

    /**
     * Retrieve all salary records.
     */
    @Transactional(readOnly = true)
    public List<SalaryDTO> getAllSalaries() {
        return salaryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calculate total payroll (sum of all total salaries).
     */
    @Transactional(readOnly = true)
    public Double calculateTotalPayroll() {
        return salaryRepository.findAll().stream()
                .mapToDouble(Salary::getTotalSalary)
                .sum();
    }

    /**
     * Converts Salary entity to SalaryDTO.
     */
    private SalaryDTO convertToDTO(Salary salary) {
        return new SalaryDTO(
                salary.getSalaryId(),
                salary.getEmployee().getEmpId(),
                salary.getEmployee().getName(),
                salary.getBasicSalary(),
                salary.getBonus(),
                salary.getTotalSalary()
        );
    }
}
