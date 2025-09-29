package com.example.employee_creator.employee.dtos;

import java.time.LocalDate;

import com.example.employee_creator.contract.entities.Contract;
import com.example.employee_creator.employee.entities.Employee;

public record EnrichedEmployeeDto(Long id, String firstName, String lastName,
        LocalDate dateOfBirth, String departmentName, String jobTitle, Float salary) {

    public static EnrichedEmployeeDto fromEntity(Employee e) {
        Contract latest = e.getContracts().stream().filter(c -> c.isActive()).findFirst().orElse(null);
        String departmentName = latest == null ? null : latest.getDepartment().getName();
        String jobTitle = latest == null ? null : latest.getJobTitle();
        Float salary = latest == null ? null : latest.getSalary();

        return new EnrichedEmployeeDto(e.getId(), e.getFirstName(), e.getLastName(), e.getDateOfBirth(), departmentName,
                jobTitle, salary);
    }
}
