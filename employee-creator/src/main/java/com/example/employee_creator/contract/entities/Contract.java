package com.example.employee_creator.contract.entities;

import java.time.LocalDate;

import com.example.employee_creator.common.entities.BaseEntity;
import com.example.employee_creator.department.entities.Department;
import com.example.employee_creator.employee.entities.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "contracts")
public class Contract extends BaseEntity {

    @Column
    private String jobTitle;
    @Column
    private Float salary;
    @Column
    private LocalDate startDate;
    @Column
    private LocalDate endDate;

    @ManyToOne
    private Employee employee;

    @ManyToOne
    private Department department;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

}
