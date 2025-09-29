package com.example.employee_creator.contract.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import jakarta.validation.constraints.Pattern;

public record UpdateContractDto(
        @Pattern(regexp = ".*\\S.*", message = "jobTitle cannot be empty") String jobTitle,
        @DecimalMin(value = "10000.00", inclusive = true) Float salary,
        LocalDate startDate,
        LocalDate endDate,
        @Min(1) Long employeeId,
        @Min(1) Long departmentId

) {
}
