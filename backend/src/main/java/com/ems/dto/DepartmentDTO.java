package com.ems.dto;

/**
 * Data Transfer Object for Department details.
 */
public class DepartmentDTO {
    private Long deptId;
    private String deptName;
    private String managerName;
    private Long employeeCount; // Optional helper for dashboard display

    public DepartmentDTO() {
    }

    public DepartmentDTO(Long deptId, String deptName, String managerName) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.managerName = managerName;
    }

    public DepartmentDTO(Long deptId, String deptName, String managerName, Long employeeCount) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.managerName = managerName;
        this.employeeCount = employeeCount;
    }

    // Getters and Setters
    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public Long getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Long employeeCount) {
        this.employeeCount = employeeCount;
    }
}
