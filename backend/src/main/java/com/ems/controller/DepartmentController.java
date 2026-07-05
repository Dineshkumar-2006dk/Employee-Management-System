package com.ems.controller;

import com.ems.dto.ApiResponse;
import com.ems.dto.DepartmentDTO;
import com.ems.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller handling REST endpoints for Department Management.
 */
@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * Get all departments.
     */
    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    /**
     * Get a specific department by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    /**
     * Create a new department.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentDTO>> createDepartment(@RequestBody DepartmentDTO dto) {
        try {
            DepartmentDTO created = departmentService.createDepartment(dto);
            return new ResponseEntity<>(
                    new ApiResponse<>(true, "Department created successfully!", created),
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Update an existing department.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDTO dto) {
        try {
            DepartmentDTO updated = departmentService.updateDepartment(id, dto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Department updated successfully!", updated));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Delete a department.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Department deleted successfully!"));
    }
}
