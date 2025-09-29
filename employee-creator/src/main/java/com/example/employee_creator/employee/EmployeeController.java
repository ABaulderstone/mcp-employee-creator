package com.example.employee_creator.employee;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee_creator.employee.dtos.CreateEmployeeDto;
import com.example.employee_creator.employee.dtos.EmployeeDto;
import com.example.employee_creator.employee.dtos.EmployeeSearchFilterDto;
import com.example.employee_creator.employee.dtos.EnrichedEmployeeDto;
import com.example.employee_creator.employee.entities.Employee;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.employee_creator.common.PageResponseAssembler;
import com.example.employee_creator.common.dtos.PageResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/employees")
@Tag(name = "Employees", description = "Employee management endpoints")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final PageResponseAssembler prAssembler;

    public EmployeeController(EmployeeService employeeService, PageResponseAssembler prAssembler) {
        this.employeeService = employeeService;
        this.prAssembler = prAssembler;
    }

    @GetMapping()
    public ResponseEntity<PageResponse<EmployeeDto>> getEmployees(@ModelAttribute EmployeeSearchFilterDto filter) {
        PageRequest pageable = PageRequest.of(filter.page() - 1, filter.size());
        Page<Employee> data = this.employeeService.findEmployees(pageable, filter);
        PageResponse<EmployeeDto> pr = prAssembler.toPageResponse(data, EmployeeDto::fromEntity);
        return ResponseEntity.ok(pr);

    }

    @PostMapping()
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody CreateEmployeeDto data) {
        Employee newEmployee = this.employeeService.create(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(EmployeeDto.fromEntity(newEmployee));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrichedEmployeeDto> getEmployeeById(@PathVariable Long id) throws BadRequestException {
        Employee found = this.employeeService.findById(id)
                .orElseThrow(() -> new BadRequestException("Could not find employee with id " + id));
        EnrichedEmployeeDto dto = EnrichedEmployeeDto.fromEntity(found);
        return ResponseEntity.ok(dto);
    }

}
