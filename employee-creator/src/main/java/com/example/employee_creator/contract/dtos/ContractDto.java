package com.example.employee_creator.contract.dtos;

import java.time.LocalDate;

import com.example.employee_creator.contract.entities.Contract;

public record ContractDto(Long id, String departmentName, String jobTitle, Float salary, LocalDate startDate,
        LocalDate endDate, boolean isActive) {

    public static ContractDto fromEntity(Contract c) {
        return new ContractDto(c.getId(), c.getDepartment().getName(), c.getJobTitle(), c.getSalary(), c.getStartDate(),
                c.getEndDate(), c.isActive());
    }
}