package com.ems.service;

import com.ems.dto.EmployeeDTO;
import com.ems.entity.Department;
import com.ems.entity.Employee;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service class handling logic for Employee Management.
 */
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SalaryRepository salaryRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Creates a new employee record.
     */
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO dto) {
        validateEmployeeDetails(dto);

        Department department = null;
        if (dto.getDeptId() != null) {
            department = departmentRepository.findById(dto.getDeptId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + dto.getDeptId()));
        }

        Employee employee = new Employee(
                dto.getName().trim(),
                dto.getEmail().trim().toLowerCase(),
                dto.getPhone().trim(),
                department,
                dto.getPosition().trim(),
                dto.getJoiningDate()
        );

        Employee saved = employeeRepository.save(employee);
        return convertToDTO(saved);
    }

    /**
     * Retrieves all employee records.
     */
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single employee by ID.
     */
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        return convertToDTO(employee);
    }

    /**
     * Searches employees by Name or ID.
     */
    @Transactional(readOnly = true)
    public List<EmployeeDTO> searchEmployees(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllEmployees();
        }
        
        String trimmedQuery = query.trim();
        List<Employee> results = new ArrayList<>();

        // If the query is a number, try to search by ID first
        if (trimmedQuery.matches("\\d+")) {
            Long id = Long.parseLong(trimmedQuery);
            employeeRepository.findById(id).ifPresent(results::add);
        }

        // Search by name matching
        List<Employee> byName = employeeRepository.findByNameContainingIgnoreCase(trimmedQuery);
        for (Employee emp : byName) {
            if (results.stream().noneMatch(e -> e.getEmpId().equals(emp.getEmpId()))) {
                results.add(emp);
            }
        }

        return results.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Updates an employee's details.
     */
    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        validateEmployeeDetails(dto);

        Department department = null;
        if (dto.getDeptId() != null) {
            department = departmentRepository.findById(dto.getDeptId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + dto.getDeptId()));
        }

        employee.setName(dto.getName().trim());
        employee.setEmail(dto.getEmail().trim().toLowerCase());
        employee.setPhone(dto.getPhone().trim());
        employee.setDepartment(department);
        employee.setPosition(dto.getPosition().trim());
        employee.setJoiningDate(dto.getJoiningDate());

        Employee updated = employeeRepository.save(employee);
        return convertToDTO(updated);
    }

    /**
     * Deletes an employee and removes their associated salary details.
     */
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        // Delete associated salary record first if it exists to maintain integrity
        salaryRepository.findByEmployeeEmpId(id).ifPresent(salary -> {
            salaryRepository.delete(salary);
        });

        employeeRepository.delete(employee);
    }

    /**
     * Validates employee fields.
     */
    private void validateEmployeeDetails(EmployeeDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name is required.");
        }
        if (dto.getEmail() == null || !EMAIL_PATTERN.matcher(dto.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("A valid email address is required.");
        }
        if (dto.getPhone() == null || dto.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required.");
        }
        if (dto.getPosition() == null || dto.getPosition().trim().isEmpty()) {
            throw new IllegalArgumentException("Job position is required.");
        }
        if (dto.getJoiningDate() == null) {
            throw new IllegalArgumentException("Joining date is required.");
        }
    }

    /**
     * Converts Employee entity to EmployeeDTO.
     */
    public EmployeeDTO convertToDTO(Employee employee) {
        Long deptId = null;
        String deptName = "Unassigned";

        if (employee.getDepartment() != null) {
            deptId = employee.getDepartment().getDeptId();
            deptName = employee.getDepartment().getDeptName();
        }

        return new EmployeeDTO(
                employee.getEmpId(),
                employee.getName(),
                employee.getEmail(),
                employee.getPhone(),
                deptId,
                deptName,
                employee.getPosition(),
                employee.getJoiningDate()
        );
    }
}
