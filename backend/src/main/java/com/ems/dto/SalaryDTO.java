package com.ems.dto;

/**
 * Data Transfer Object for Salary details.
 */
public class SalaryDTO {
    private Long salaryId;
    private Long empId;
    private String employeeName;
    private Double basicSalary;
    private Double bonus;
    private Double totalSalary;

    public SalaryDTO() {
    }

    public SalaryDTO(Long salaryId, Long empId, String employeeName, Double basicSalary, Double bonus, Double totalSalary) {
        this.salaryId = salaryId;
        this.empId = empId;
        this.employeeName = employeeName;
        this.basicSalary = basicSalary;
        this.bonus = bonus;
        this.totalSalary = totalSalary;
    }

    // Getters and Setters
    public Long getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(Long salaryId) {
        this.salaryId = salaryId;
    }

    public Long getEmpId() {
        return empId;
    }

    public void setEmpId(Long empId) {
        this.empId = empId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(Double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public Double getBonus() {
        return bonus;
    }

    public void setBonus(Double bonus) {
        this.bonus = bonus;
    }

    public Double getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(Double totalSalary) {
        this.totalSalary = totalSalary;
    }
}
