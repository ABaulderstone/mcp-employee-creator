package com.example.employee_creator.employee.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Pattern;

public record UpdateEmployeeDto(
        @Pattern(regexp = ".*\\S.*", message = "firstName cannot be empty") String firstName,
        @Pattern(regexp = ".*\\S.*", message = "lastName cannot be empty") String lastName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate dateOfBirth) {

}
