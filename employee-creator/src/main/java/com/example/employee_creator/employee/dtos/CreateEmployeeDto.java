package com.example.employee_creator.employee.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;

public record CreateEmployeeDto(
                @NotBlank String firstName,
                @NotBlank String lastName,
                @NotBlank @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate dateOfBirth) {

}
