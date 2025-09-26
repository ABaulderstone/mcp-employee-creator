package com.example.employee_creator.contract.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateContractDto(
        @NotBlank String jobTitle,
        @NotNull @DecimalMin(value = "10000.00", inclusive = true) Float salary,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        @NotNull @Min(1) Long employeeId,
        @NotNull @Min(1) Long departmentId

) {

}
