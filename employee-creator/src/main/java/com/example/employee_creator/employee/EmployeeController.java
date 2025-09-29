package com.example.employee_creator.employee;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee_creator.employee.dtos.CreateEmployeeDto;
import com.example.employee_creator.employee.dtos.EmployeeDto;
import com.example.employee_creator.employee.dtos.EmployeeSearchFilterDto;
import com.example.employee_creator.employee.dtos.EnrichedEmployeeDto;
import com.example.employee_creator.employee.dtos.UpdateEmployeeDto;
import com.example.employee_creator.employee.entities.Employee;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.employee_creator.common.PageResponseAssembler;
import com.example.employee_creator.common.dtos.PageResponse;
import com.example.employee_creator.contract.dtos.ContractDto;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/{id}/contracts")
    public ResponseEntity<List<ContractDto>> getContracts(@PathVariable Long id) throws BadRequestException {
        Employee found = this.employeeService.findById(id)
                .orElseThrow(() -> new BadRequestException("Could not find employee with id " + id));
        List<ContractDto> contracts = found.getContracts().stream().map(ContractDto::fromEntity).toList();
        return ResponseEntity.ok(contracts);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EnrichedEmployeeDto> updateEmployeeById(@PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeDto data) throws BadRequestException {
        Employee updated = this.employeeService.updateById(id, data)
                .orElseThrow(() -> new BadRequestException("Could not find employee with id " + id));
        return ResponseEntity.ok(EnrichedEmployeeDto.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployeeById(@PathVariable Long id) throws BadRequestException {
        boolean isDeleted = this.employeeService.deleteById(id);
        if (!isDeleted) {
            throw new BadRequestException("Could not find employee with id " + id);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
