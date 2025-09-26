package com.example.employee_creator.employee;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee_creator.employee.dtos.CreateEmployeeDto;
import com.example.employee_creator.employee.entities.Employee;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping()
    public String createEmployee(@Valid @RequestBody CreateEmployeeDto data) {
        Employee newEmployee = this.employeeService.create(data);
        return "x";
    }

}
