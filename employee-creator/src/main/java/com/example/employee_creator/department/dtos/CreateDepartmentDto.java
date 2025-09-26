package com.example.employee_creator.department.dtos;

import jakarta.validation.constraints.NotBlank;

public record CreateDepartmentDto(@NotBlank String name, @NotBlank String description) {

}
