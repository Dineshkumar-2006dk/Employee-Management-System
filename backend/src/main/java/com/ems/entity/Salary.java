package com.ems.entity;

import jakarta.persistence.*;

/**
 * Entity representing the salary table.
 */
@Entity
@Table(name = "salary")
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_id")
    private Long salaryId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_id", nullable = false, unique = true)
    private Employee employee;

    @Column(name = "basic_salary", nullable = false)
    private Double basicSalary;

    @Column(nullable = false)
    private Double bonus;

    @Column(name = "total_salary", nullable = false)
    private Double totalSalary;

    // Constructors
    public Salary() {
    }

    public Salary(Employee employee, Double basicSalary, Double bonus) {
        this.employee = employee;
        this.basicSalary = basicSalary;
        this.bonus = bonus;
        calculateTotalSalary();
    }

    // Helper method to automatically calculate total salary
    public void calculateTotalSalary() {
        double basic = this.basicSalary != null ? this.basicSalary : 0.0;
        double bon = this.bonus != null ? this.bonus : 0.0;
        this.totalSalary = basic + bon;
    }

    // Getters and Setters (Encapsulation)
    public Long getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(Long salaryId) {
        this.salaryId = salaryId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(Double basicSalary) {
        this.basicSalary = basicSalary;
        calculateTotalSalary();
    }

    public Double getBonus() {
        return bonus;
    }

    public void setBonus(Double bonus) {
        this.bonus = bonus;
        calculateTotalSalary();
    }

    public Double getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(Double totalSalary) {
        this.totalSalary = totalSalary;
    }
}
