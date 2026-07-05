package com.ems.controller;

import com.ems.dto.ApiResponse;
import com.ems.dto.SalaryDTO;
import com.ems.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller handling REST endpoints for Salary Management.
 */
@RestController
@RequestMapping("/api/salaries")
@CrossOrigin(origins = "*")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    /**
     * Get all salary records.
     */
    @GetMapping
    public ResponseEntity<List<SalaryDTO>> getAllSalaries() {
        return ResponseEntity.ok(salaryService.getAllSalaries());
    }

    /**
     * Get salary details for a specific employee.
     */
    @GetMapping("/employee/{empId}")
    public ResponseEntity<SalaryDTO> getSalaryByEmployeeId(@PathVariable Long empId) {
        return ResponseEntity.ok(salaryService.getSalaryByEmployeeId(empId));
    }

    /**
     * Set or add salary details.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SalaryDTO>> saveSalary(@RequestBody SalaryDTO dto) {
        try {
            SalaryDTO saved = salaryService.saveSalary(dto);
            return new ResponseEntity<>(
                    new ApiResponse<>(true, "Salary details saved successfully!", saved),
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
     * Update an existing salary record.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SalaryDTO>> updateSalary(@PathVariable Long id, @RequestBody SalaryDTO dto) {
        try {
            SalaryDTO updated = salaryService.updateSalary(id, dto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Salary details updated successfully!", updated));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Get total payroll cost (sum of all total salaries).
     */
    @GetMapping("/payroll")
    public ResponseEntity<Double> getPayrollStats() {
        return ResponseEntity.ok(salaryService.calculateTotalPayroll());
    }
}
