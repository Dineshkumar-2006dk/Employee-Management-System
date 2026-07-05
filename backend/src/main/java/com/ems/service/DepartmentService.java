package com.ems.service;

import com.ems.dto.DepartmentDTO;
import com.ems.entity.Department;
import com.ems.entity.Employee;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class handling logic for Department Management.
 */
@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Create a new department.
     */
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO dto) {
        if (dto.getDeptName() == null || dto.getDeptName().trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be empty.");
        }
        Department dept = new Department(
                dto.getDeptName().trim(),
                dto.getManagerName() != null ? dto.getManagerName().trim() : ""
        );
        Department saved = departmentRepository.save(dept);
        return convertToDTO(saved);
    }

    /**
     * Retrieve all departments.
     */
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        List<Employee> employees = employeeRepository.findAll();

        return departments.stream().map(dept -> {
            // Count number of employees in this department
            long count = employees.stream()
                    .filter(emp -> emp.getDepartment() != null && emp.getDepartment().getDeptId().equals(dept.getDeptId()))
                    .count();
            return new DepartmentDTO(
                    dept.getDeptId(),
                    dept.getDeptName(),
                    dept.getManagerName(),
                    count
            );
        }).collect(Collectors.toList());
    }

    /**
     * Retrieve department by ID.
     */
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentById(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
        
        long count = employeeRepository.findAll().stream()
                .filter(emp -> emp.getDepartment() != null && emp.getDepartment().getDeptId().equals(dept.getDeptId()))
                .count();

        return new DepartmentDTO(dept.getDeptId(), dept.getDeptName(), dept.getManagerName(), count);
    }

    /**
     * Update department details.
     */
    @Transactional
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO dto) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        if (dto.getDeptName() == null || dto.getDeptName().trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be empty.");
        }

        dept.setDeptName(dto.getDeptName().trim());
        dept.setManagerName(dto.getManagerName() != null ? dto.getManagerName().trim() : "");

        Department updated = departmentRepository.save(dept);
        return getDepartmentById(updated.getDeptId());
    }

    /**
     * Delete department and clean up employee associations.
     */
    @Transactional
    public void deleteDepartment(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        // Find all employees associated with this department and set their department to null
        List<Employee> employees = employeeRepository.findAll();
        for (Employee emp : employees) {
            if (emp.getDepartment() != null && emp.getDepartment().getDeptId().equals(id)) {
                emp.setDepartment(null);
                employeeRepository.save(emp);
            }
        }

        departmentRepository.delete(dept);
    }

    /**
     * Converts Department entity to DepartmentDTO.
     */
    private DepartmentDTO convertToDTO(Department dept) {
        return new DepartmentDTO(
                dept.getDeptId(),
                dept.getDeptName(),
                dept.getManagerName(),
                0L
        );
    }
}
