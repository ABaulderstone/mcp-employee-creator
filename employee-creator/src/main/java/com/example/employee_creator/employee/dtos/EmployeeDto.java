package com.example.employee_creator.employee.dtos;

import com.example.employee_creator.employee.entities.Employee;

public record EmployeeDto(Long id, String firstName, String lastName, String email) {
    public static EmployeeDto fromEntity(Employee e) {
        return new EmployeeDto(e.getId(), e.getFirstName(), e.getLastName(), e.getEmail());
    }
}
