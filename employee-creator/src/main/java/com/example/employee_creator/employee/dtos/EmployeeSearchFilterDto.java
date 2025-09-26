package com.example.employee_creator.employee.dtos;

public record EmployeeSearchFilterDto(Integer page, Integer size, String department, String searchTerm,
        String searchBy) {
    public EmployeeSearchFilterDto {
        page = (page == null) ? 1 : page;
        size = (size == null) ? 10 : size;
    }
}
