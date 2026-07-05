package com.ems.entity;

import jakarta.persistence.*;

/**
 * Entity representing the departments table.
 */
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Long deptId;

    @Column(name = "dept_name", nullable = false)
    private String deptName;

    @Column(name = "manager_name")
    private String managerName;

    // Constructors
    public Department() {
    }

    public Department(String deptName, String managerName) {
        this.deptName = deptName;
        this.managerName = managerName;
    }

    // Getters and Setters (Encapsulation)
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
}
