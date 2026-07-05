package com.ems.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object for Employee details.
 */
public class EmployeeDTO {
    private Long empId;
    private String name;
    private String email;
    private String phone;
    private Long deptId;
    private String deptName;
    private String position;
    private LocalDate joiningDate;

    public EmployeeDTO() {
    }

    public EmployeeDTO(Long empId, String name, String email, String phone, Long deptId, String deptName, String position, LocalDate joiningDate) {
        this.empId = empId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.deptId = deptId;
        this.deptName = deptName;
        this.position = position;
        this.joiningDate = joiningDate;
    }

    // Getters and Setters
    public Long getEmpId() {
        return empId;
    }

    public void setEmpId(Long empId) {
        this.empId = empId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }
}
